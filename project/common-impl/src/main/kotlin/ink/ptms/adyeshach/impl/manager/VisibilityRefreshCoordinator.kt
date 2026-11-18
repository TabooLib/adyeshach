package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.manager.PlayerManager
import ink.ptms.adyeshach.core.util.FoliaSupport
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import ink.ptms.adyeshach.impl.ServerTours
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import ink.ptms.adyeshach.impl.util.EntityThreads
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

/**
 * 两阶段可见性刷新协调器
 * 主线程采集轻量快照，异步计算实体 × 玩家候选，结果回主线程复核后提交 spawn / destroy。
 * 单航班 + epoch 保证异步任务不堆积，过期结果不会提交。
 *
 * @author sky
 */
object VisibilityRefreshCoordinator {

    private const val ALL_PLAYERS = -1

    /**
     * 当前游戏刻的已 setup 玩家列表
     * 由可见性周期启动时刷新，供 ViewPlayers / hide 等路径复用，避免重复扫在线玩家。
     */
    @Volatile
    var playersInGameTick: Collection<Player> = listOf()

    // 单航班可见性周期；非空时跳过新的全量计算，避免异步任务堆积。
    @Volatile
    private var visibilityCycle: VisibilityCycle? = null

    // 每次成功启动周期时递增，主线程只接受当前 epoch 的异步结果。
    @Volatile
    private var visibilityEpoch = 0L

    /**
     * 在主线程启动一次两阶段可见性周期
     * 若上周期结果已回邮箱则先提交；若仍在飞行则跳过，保证单航班。
     */
    fun startVisibilityCycle() {
        val currentCycle = visibilityCycle
        if (currentCycle != null) {
            val result = currentCycle.result.get()
            if (result != null) {
                commitVisibilityCandidates(currentCycle.epoch, result)
            }
            return
        }
        val setupPlayers = Bukkit.getOnlinePlayers().filter { it.hasMetadata("adyeshach_setup") }
        playersInGameTick = setupPlayers
        val playerSnapshots = setupPlayers.map { player ->
            val location = player.location
            PlayerVisibilitySnapshot(
                uniqueId = player.uniqueId,
                name = player.name,
                world = location.world?.name,
                x = location.x,
                z = location.z,
                routePlaying = ServerTours.isRoutePlaying(player),
            )
        }
        val contexts = ArrayList<EntityVisibilityContext>()
        val entitySnapshots = ArrayList<EntityVisibilitySnapshot>()
        // Folia：载具位置校准需要在实体所属线程执行，这里只收集，快照完成后统一投递
        val prepareBatch = if (FoliaSupport.isFolia) EntityThreads.Batch<DefaultEntityInstance>() else null
        // 公共管理器
        captureManagerVisibility(
            manager = DefaultAdyeshachBooster.api.localPublicEntityManager,
            playerIndex = ALL_PLAYERS,
            contexts = contexts,
            snapshots = entitySnapshots,
            prepareBatch = prepareBatch,
        )
        captureManagerVisibility(
            manager = DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary,
            playerIndex = ALL_PLAYERS,
            contexts = contexts,
            snapshots = entitySnapshots,
            prepareBatch = prepareBatch,
        )
        // 私有管理器
        setupPlayers.forEachIndexed { playerIndex, player ->
            val manager = DefaultAdyeshachAPI.playerEntityTemporaryManagerMap[player] ?: return@forEachIndexed
            captureManagerVisibility(
                manager = manager,
                playerIndex = playerIndex,
                contexts = contexts,
                snapshots = entitySnapshots,
                prepareBatch = prepareBatch,
            )
        }
        val epoch = ++visibilityEpoch
        prepareBatch?.dispatch { list ->
            list.forEach { EntityThreads.runSafely { if (!it.isRemoved) it.visibilityHandler.prepareVisibilityCycle() } }
        }
        val cycle = VisibilityCycle(epoch, contexts)
        visibilityCycle = cycle
        try {
            submitAsync {
                val candidates = try {
                    calculateVisibilityCandidates(playerSnapshots, entitySnapshots)
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                    emptyList()
                }
                // 先发布纯计算结果；主线程回调投递失败时由下一次周期消费，避免单航班永久占用。
                cycle.result.set(candidates)
                try {
                    submit { commitVisibilityCandidates(epoch, candidates) }
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }
            }
        } catch (ex: Throwable) {
            discardVisibilityCycle(epoch)
            ex.printStackTrace()
        }
    }

    /**
     * 使进行中的可见性周期立即失效
     * 插件禁用时调用，尚未回到主线程的候选不会提交。
     */
    fun invalidate() {
        visibilityEpoch++
        visibilityCycle = null
    }

    /**
     * 准备实体的可见性检查
     * 非 Folia：在主线程直接执行（原有逻辑）
     * Folia：当前为全局区域线程，载具校准投递到实体所属线程；伴生实体仍跳过玩家距离扫描
     */
    private fun prepareVisibilityCycle(entity: DefaultEntityInstance, prepareBatch: EntityThreads.Batch<DefaultEntityInstance>?): Boolean {
        if (prepareBatch == null) {
            return entity.visibilityHandler.prepareVisibilityCycle()
        }
        if (!entity.isDisableVehicleCheckOnTick && (entity.cacheVehicleEntity != null || entity.hasPersistentTag(StandardTags.IS_IN_VEHICLE))) {
            prepareBatch.add(entity, entity)
        }
        return !entity.isCompanion()
    }

    /**
     * 在主线程同步载具位置并采集基础字段快照与并发可见性集合只读引用
     */
    private fun captureManagerVisibility(
        manager: DefaultManager,
        playerIndex: Int,
        contexts: MutableList<EntityVisibilityContext>,
        snapshots: MutableList<EntityVisibilitySnapshot>,
        prepareBatch: EntityThreads.Batch<DefaultEntityInstance>?,
    ) {
        manager.activeEntity.forEach { rawEntity ->
            val entity = rawEntity as DefaultEntityInstance
            if (entity.isRemoved || !prepareVisibilityCycle(entity, prepareBatch)) {
                return@forEach
            }
            val position = entity.clientPosition
            val slot = contexts.size
            contexts += EntityVisibilityContext(manager, entity)
            snapshots += EntityVisibilitySnapshot(
                slot = slot,
                playerIndex = playerIndex,
                world = position.world.name,
                x = position.x,
                z = position.z,
                visibleDistance = entity.visibleDistance,
                hidden = entity.isHide(),
                // 并发 key set 的弱一致 contains 只生成候选；主线程提交前仍会复核 ACL 与 visible。
                viewers = entity.viewPlayers.viewers,
                visible = entity.viewPlayers.visible,
            )
        }
    }

    /**
     * 仅根据位置与状态快照、并发集合弱一致读计算需要变化的实体 × 玩家候选
     */
    private fun calculateVisibilityCandidates(
        players: List<PlayerVisibilitySnapshot>,
        entities: List<EntityVisibilitySnapshot>,
    ): List<VisibilityCandidate> {
        val candidates = ArrayList<VisibilityCandidate>()
        entities.forEach { entity ->
            if (entity.playerIndex == ALL_PLAYERS) {
                players.forEach { player -> calculateVisibilityCandidate(entity, player, candidates) }
            } else {
                val player = players.getOrNull(entity.playerIndex) ?: return@forEach
                calculateVisibilityCandidate(entity, player, candidates)
            }
        }
        return candidates
    }

    /**
     * 仅根据快照判定单个实体与玩家是否需要显隐变化
     */
    private fun calculateVisibilityCandidate(
        entity: EntityVisibilitySnapshot,
        player: PlayerVisibilitySnapshot,
        candidates: MutableList<VisibilityCandidate>,
    ) {
        if (player.name !in entity.viewers) {
            return
        }
        val currentlyVisible = player.name in entity.visible
        val inVisibleDistance = player.world == entity.world && entity.visibleDistance >= 0.0 && run {
            val deltaX = player.x - entity.x
            val deltaZ = player.z - entity.z
            deltaX * deltaX + deltaZ * deltaZ < entity.visibleDistance * entity.visibleDistance
        }
        val targetVisible = if (currentlyVisible) {
            !entity.hidden && (inVisibleDistance || player.routePlaying)
        } else {
            !entity.hidden && inVisibleDistance
        }
        if (targetVisible != currentlyVisible) {
            candidates += VisibilityCandidate(entity.slot, player.uniqueId, player.name, currentlyVisible, targetVisible)
        }
    }

    /**
     * 在主线程复核最新周期候选并触发真实 spawn / destroy
     */
    private fun commitVisibilityCandidates(epoch: Long, candidates: List<VisibilityCandidate>) {
        val cycle = visibilityCycle
        if (cycle == null || cycle.epoch != epoch || visibilityEpoch != epoch) {
            return
        }
        try {
            // 非 Folia：在主线程按顺序提交；Folia：按实体所属线程分组投递后提交
            EntityThreads.forEach(candidates, { cycle.entities[it.entitySlot].entity }) { candidate ->
                val context = cycle.entities.getOrNull(candidate.entitySlot) ?: return@forEach
                val entity = context.entity
                val manager = context.manager
                if (entity.isRemoved || entity.manager !== manager || entity !in manager.activeEntity || !manager.isValid()) {
                    return@forEach
                }
                val player = Bukkit.getPlayer(candidate.playerUniqueId) ?: return@forEach
                if (!player.isOnline || !player.hasMetadata("adyeshach_setup") || player.name != candidate.playerName) {
                    return@forEach
                }
                if (manager is PlayerManager &&
                    (manager.owner.uniqueId != player.uniqueId || DefaultAdyeshachAPI.playerEntityTemporaryManagerMap[player] !== manager)
                ) {
                    return@forEach
                }
                entity.visibilityHandler.commitVisibleCandidate(
                    player = player,
                    expectedVisible = candidate.expectedVisible,
                    visible = candidate.visible,
                )
            }
        } finally {
            discardVisibilityCycle(epoch)
        }
    }

    /**
     * 在主线程结束指定周期，旧 epoch 不得清除更新的周期
     */
    private fun discardVisibilityCycle(epoch: Long) {
        if (visibilityCycle?.epoch == epoch) {
            visibilityCycle = null
        }
    }

    private class VisibilityCycle(
        val epoch: Long,
        val entities: List<EntityVisibilityContext>,
    ) {

        // 异步计算结果邮箱；主线程投递失败时由下一周期消费。
        val result = AtomicReference<List<VisibilityCandidate>?>()
    }

    private data class EntityVisibilityContext(
        val manager: DefaultManager,
        val entity: DefaultEntityInstance,
    )

    private data class PlayerVisibilitySnapshot(
        val uniqueId: UUID,
        val name: String,
        val world: String?,
        val x: Double,
        val z: Double,
        val routePlaying: Boolean,
    )

    private data class EntityVisibilitySnapshot(
        val slot: Int,
        val playerIndex: Int,
        val world: String,
        val x: Double,
        val z: Double,
        val visibleDistance: Double,
        val hidden: Boolean,
        val viewers: Set<String>,
        val visible: Set<String>,
    )

    private data class VisibilityCandidate(
        val entitySlot: Int,
        val playerUniqueId: UUID,
        val playerName: String,
        val expectedVisible: Boolean,
        val visible: Boolean,
    )
}
