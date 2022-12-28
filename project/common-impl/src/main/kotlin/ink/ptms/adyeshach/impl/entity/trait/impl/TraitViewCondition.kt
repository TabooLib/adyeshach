package ink.ptms.adyeshach.impl.entity.trait.impl

import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.impl.entity.trait.Trait
import ink.ptms.adyeshach.impl.util.Inputs.inputBook
import org.bukkit.entity.Player
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.common5.clong
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.kether.runKether

object TraitViewCondition : Trait() {

    @SubscribeEvent
    private fun onRemove(e: AdyeshachEntityRemoveEvent) {
        data[e.entity.uniqueId] = null
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

    override fun getName(): String {
        return "view-condition"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        language.sendLang(player, "trait-view-condition")
        player.inputBook(data.getStringList(entityInstance.uniqueId)) {
            if (it.all { line -> line.isBlank() }) {
                data[entityInstance.uniqueId] = null
            } else {
                data[entityInstance.uniqueId] = it
            }
            entityInstance.despawn()
            entityInstance.respawn()
            language.sendLang(player, "trait-view-condition-finish")
        }
    }

    fun checkView(entity: EntityInstance, viewer: Player): Boolean {
        if (data.contains(entity.uniqueId)) {
            try {
                val script = data.getStringList(entity.uniqueId)
                return Coerce.toBoolean(KetherShell.eval(script, namespace = listOf("adyeshach"), sender = adaptPlayer(viewer)) {
                    rootFrame().variables()["@entities"] = listOf(entity)
                }.getNow(false))
            } catch (ex: Exception) {
                ex.printKetherErrorMessage()
            }
        }
        return true
    }
}

fun EntityInstance.setViewCondition(condition: List<String>?) {
    if (condition == null || condition.all { line -> line.isBlank() }) {
        TraitViewCondition.data[uniqueId] = null
    } else {
        TraitViewCondition.data[uniqueId] = condition
        despawn()
        respawn()
    }
}

fun EntityInstance.updateViewCondition() {
    val nextCheckTime = getTag("view-condition-next-check")?.clong ?: 0
    if (nextCheckTime > System.currentTimeMillis()) {
        return
    }
    val nextUpdateTime = getTag("view-condition-next-update")?.clong ?: 0
    if (nextUpdateTime > System.currentTimeMillis()) {
        return
    }
    // 持有观察条件
    if (TraitViewCondition.data.contains(uniqueId)) {
        // 获取条件
        val script = TraitViewCondition.data.getStringList(uniqueId)
        // 设置冷却
        setTag("view-condition-next-update", (System.currentTimeMillis() + (AdyeshachSettings.viewConditionInterval * 50)).toString())
        // 获取玩家
        viewPlayers.getPlayersInViewDistance().forEach {
            runKether {
                KetherShell.eval(script, namespace = listOf("adyeshach"), sender = adaptPlayer(it)) {
                    rootFrame().variables()["@entities"] = listOf(this)
                }.thenAccept { cond ->
                    if (Coerce.toBoolean(cond)) {
                        // 看不见但是满足可视条件
                        if (it.name !in viewPlayers.visible) {
                            visible(it, true)
                        }
                    } else {
                        // 看得见但不满足可视条件
                        if (it.name in viewPlayers.visible) {
                            visible(it, false)
                            viewPlayers.visible.remove(it.name)
                        }
                    }
                }
            }
        }
    } else {
        // 若不持有观察条件则在一段时间后检测
        setTag("view-condition-next-check", (System.currentTimeMillis() + 5000).toString())
    }
}