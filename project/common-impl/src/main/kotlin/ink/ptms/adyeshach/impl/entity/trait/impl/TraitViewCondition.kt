package ink.ptms.adyeshach.impl.entity.trait.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.impl.entity.trait.Trait
import ink.ptms.adyeshach.impl.util.Inputs.inputBook
import org.bukkit.entity.Player
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.random
import taboolib.common5.cbool
import taboolib.common5.clong
import taboolib.module.kether.KetherShell
import taboolib.module.kether.bool
import taboolib.module.kether.runKether
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

object TraitViewCondition : Trait() {

    /** 检查标签 */
    const val CHECK_TAG = "VIEW_CONDITION_NEXT_CHECK"

    /** 每个实体当前可见条件检查版本，旧 Future 结果不得覆盖新检查。 */
    val checkVersions = ConcurrentHashMap<String, Long>()

    /** Kether 异步结果的主线程提交队列，避免为每个实体和玩家分别创建调度任务。 */
    val pendingVisibilityUpdates = ConcurrentLinkedQueue<Runnable>()

    /**
     * 在主线程批量提交已完成的可见条件结果
     */
    @Schedule(period = 1)
    fun flushVisibilityUpdates() {
        while (true) {
            val update = pendingVisibilityUpdates.poll() ?: return
            try {
                update.run()
            } catch (ex: Throwable) {
                // 单个脚本结果异常不能阻断后续队列，也不能让周期任务被调度器取消。
                ex.printStackTrace()
            }
        }
    }

    @Schedule(period = 20)
    fun update() {
        Adyeshach.api().getPublicEntityManager(ManagerType.PERSISTENT).getEntities { !it.isDerived() }.forEach { it.updateTraitViewCondition() }
    }

    @SubscribeEvent
    private fun onRemove(e: AdyeshachEntityRemoveEvent) {
        data[e.entity.uniqueId] = null
        checkVersions.remove(e.entity.uniqueId)
    }

    @SubscribeEvent(EventPriority.LOWEST)
    private fun onDamage(e: AdyeshachEntityDamageEvent) {
        if (!checkView(e.entity, e.player)) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent(EventPriority.LOWEST)
    private fun onInteract(e: AdyeshachEntityInteractEvent) {
        if (!checkView(e.entity, e.player)) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    private fun onVisible(e: AdyeshachEntityVisibleEvent) {
        if (e.visible && !checkView(e.entity, e.viewer)) {
            e.isCancelled = true
        }
    }

    override fun id(): String {
        return "view-condition"
    }

    override fun edit(player: Player, entityInstance: EntityInstance): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        language.sendLang(player, "trait-view-condition")
        player.inputBook(data.getStringList(entityInstance.uniqueId)) {
            entityInstance.setTraitViewCondition(it)
            future.complete(null)
        }
        return future
    }

    fun checkView(entity: EntityInstance, viewer: Player): Boolean {
        if (data.contains(entity.uniqueId)) {
            return runKether {
                runViewConditionScript(
                    data.getStringList(entity.uniqueId),
                    entity,
                    viewer,
                    entity
                ).getNow(false).cbool
            } ?: false
        }
        return true
    }

    /**
     * 统一执行可视条件脚本
     */
    fun runViewConditionScript(
        script: List<String>,
        entity: EntityInstance,
        viewer: Player,
        entitiesValue: Any,
    ): CompletableFuture<Any?> {
        return KetherShell.eval(
            script,
            namespace = listOf("adyeshach"),
            sender = adaptPlayer(viewer)
        ) {
            set("@entities", entitiesValue)
            set("@manager", entity.manager)
        }
    }
}

/**
 * 设置可视条件
 */
fun EntityInstance.setTraitViewCondition(condition: List<String>?) {
    removeTag(TraitViewCondition.CHECK_TAG)
    TraitViewCondition.checkVersions.compute(uniqueId) { _, version -> (version ?: 0L) + 1 }
    if (condition == null || condition.all { line -> line.isBlank() }) {
        TraitViewCondition.data[uniqueId] = null
    } else {
        TraitViewCondition.data[uniqueId] = condition
        despawn()
        respawn()
    }
}

/**
 * 获取可视条件
 */
fun EntityInstance.getTraitViewCondition(): List<String> {
    return TraitViewCondition.data.getStringList(uniqueId)
}

/**
 * 更新可视条件
 */
fun EntityInstance.updateTraitViewCondition() {
    val checkTime = getTag(TraitViewCondition.CHECK_TAG)?.clong ?: 0
    if (checkTime > System.currentTimeMillis()) {
        return
    }
    // 持有观察条件
    if (TraitViewCondition.data.contains(uniqueId)) {
        // 获取条件
        val script = TraitViewCondition.data.getStringList(uniqueId)
        val checkVersion = TraitViewCondition.checkVersions.compute(uniqueId) { _, version -> (version ?: 0L) + 1 }!!
        // 设置冷却
        setTag(TraitViewCondition.CHECK_TAG, System.currentTimeMillis() + (AdyeshachSettings.viewConditionInterval * 50))
        // 获取玩家
        viewPlayers.getPlayersInViewDistance().forEach { player ->
            runKether {
                TraitViewCondition.runViewConditionScript(
                    script,
                    this@updateTraitViewCondition,
                    player,
                    listOf(this@updateTraitViewCondition)
                ).bool { cond ->
                    // Kether Future 的回调线程不固定，可见集合与发包只能回主线程提交
                    TraitViewCondition.pendingVisibilityUpdates.add(Runnable {
                        if (TraitViewCondition.checkVersions[uniqueId] != checkVersion) {
                            return@Runnable
                        }
                        if (cond) {
                            // 看不见但是满足可视条件
                            if (player.isOnline && player.name in viewPlayers.viewers && player.name !in viewPlayers.visible
                                && isInVisibleDistance(player) && !isHide()
                                && Adyeshach.api().getMinecraftAPI().getHelper().isChunkVisible(player, chunkX, chunkZ)) {
                                visible(player, true)
                            }
                        } else {
                            // 看得见但不满足可视条件
                            if (player.name in viewPlayers.visible) {
                                visible(player, false)
                            }
                        }
                    })
                }
            }
        }
    } else {
        // 若不持有观察条件则在一段时间后检测
        setTag(TraitViewCondition.CHECK_TAG, System.currentTimeMillis() + 5000 + random(5000))
    }
}
