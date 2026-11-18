package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import ink.ptms.adyeshach.impl.util.EntityThreads
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Folia 实体 Tick 调度
 *
 * 由全局区域线程每刻调用一次，只负责把可 tick 的实体分发到所属线程：
 * - 公有实体：实体所在区块的区域线程
 * - 私有实体（只被所有者观察）：所有者的实体线程
 * - 私有多人观察实体：实体所在区块的区域线程
 *
 * 每个实体同一时间最多只有一个尚未执行的 tick 任务，区域卡顿时不会堆积，也不会在追帧时连续 tick 多次。
 */
@OptIn(ExperimentalTime::class)
object FoliaEntityTicker {

    /** 超过该刻数仍未执行的 tick 任务视为已丢弃（例如所有者离线导致实体调度器退役），允许重新投递 */
    private const val STALE_TICKS = 100L

    /** 全局刻计数，仅在全局区域线程中读写 */
    private var currentTick = 0L

    private sealed class Item {
        class Single(val entity: DefaultEntityInstance) : Item()
        class Owned(val manager: DefaultPlayerManager, val entities: List<DefaultEntityInstance>) : Item()
    }

    fun tick() {
        val tick = ++currentTick
        val batch = EntityThreads.Batch<Item>()
        // 公共管理器：区块线程
        collectPublic(DefaultAdyeshachBooster.api.localPublicEntityManager, tick, batch)
        collectPublic(DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary, tick, batch)
        // 私有管理器
        DefaultAdyeshachAPI.playerEntityTemporaryManagerMap.values().forEach managerLoop@{ manager ->
            if (!manager.isValid()) {
                return@managerLoop
            }
            val owner = manager.owner
            val owned = ArrayList<DefaultEntityInstance>()
            manager.tickableEntities.forEach entityLoop@{ entity ->
                entity as DefaultEntityInstance
                if (!tryMarkDispatched(entity, tick)) {
                    return@entityLoop
                }
                if (EntityThreads.ownerOf(entity) != null) {
                    // 私有实体：所有者线程
                    owned += entity
                } else {
                    // 私有多人观察：区块线程
                    batch.addToChunk(EntityThreads.chunkOf(entity), Item.Single(entity))
                }
            }
            if (owned.isNotEmpty()) {
                batch.addToOwner(owner, Item.Owned(manager, owned))
            }
        }
        if (batch.isEmpty()) {
            return
        }
        batch.dispatch(onDropped = { items -> items.forEach { release(it) } }) { items ->
            items.forEach { item ->
                when (item) {
                    is Item.Single -> tickEntity(item.entity)
                    is Item.Owned -> tickOwned(item)
                }
            }
        }
    }

    private fun collectPublic(manager: DefaultManager, tick: Long, batch: EntityThreads.Batch<Item>) {
        manager.tickableEntities.forEach { entity ->
            entity as DefaultEntityInstance
            if (tryMarkDispatched(entity, tick)) {
                batch.addToChunk(EntityThreads.chunkOf(entity), Item.Single(entity))
            }
        }
    }

    private fun tryMarkDispatched(entity: DefaultEntityInstance, tick: Long): Boolean {
        val dispatchedAt = entity.foliaTickDispatchedAt
        if (dispatchedAt != 0L && tick - dispatchedAt < STALE_TICKS) {
            return false
        }
        entity.foliaTickDispatchedAt = tick
        return true
    }

    private fun release(item: Item) {
        when (item) {
            is Item.Single -> item.entity.foliaTickDispatchedAt = 0L
            is Item.Owned -> item.entities.forEach { it.foliaTickDispatchedAt = 0L }
        }
    }

    private fun tickOwned(item: Item.Owned) {
        val manager = item.manager
        val time = measureTime {
            item.entities.forEach { tickEntity(it) }
        }
        // 如果处理这个玩家的时间超过 50ms 则在后台进行报告，报告周期为 5 秒 1 次。
        if (time > 50.milliseconds && manager.isValid()) {
            if (DefaultManagerHandler.isFirstReport) {
                DefaultManagerHandler.isFirstReport = false
            } else {
                if (AdyeshachSettings.debug) DefaultManagerHandler.entityReport(manager.owner, time)
            }
        }
    }

    private fun tickEntity(entity: DefaultEntityInstance) {
        try {
            if (entity.isRemoved || !isTickable(entity)) {
                return
            }
            // 事件处理
            if (DefaultAdyeshachAPI.localEventBus.callTick(entity)) {
                entity.onTick()
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        } finally {
            entity.foliaTickDispatchedAt = 0L
        }
    }

    private fun isTickable(entity: EntityInstance): Boolean {
        val manager = entity.manager as? DefaultManager ?: return false
        return manager.isValid() && entity in manager.tickableEntities
    }
}
