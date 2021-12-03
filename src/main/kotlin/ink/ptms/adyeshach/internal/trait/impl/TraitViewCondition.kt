package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.api.AdyeshachSettings
import ink.ptms.adyeshach.api.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityTickEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.Inputs.inputBook
import ink.ptms.adyeshach.internal.runKether
import ink.ptms.adyeshach.internal.trait.Trait
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.module.kether.KetherShell
import taboolib.platform.util.sendLang

object TraitViewCondition : Trait() {

    @SubscribeEvent
    fun e(e: AdyeshachEntityRemoveEvent) {
        data[e.entity.uniqueId] = null
    }

    @SubscribeEvent
    fun e(e: AdyeshachEntityVisibleEvent) {
        if (e.visible && data.contains(e.entity.uniqueId)) {
            runKether {
                val script = data.getStringList(e.entity.uniqueId)
                KetherShell.eval(script, namespace = listOf("adyeshach"), sender = adaptPlayer(e.viewer)).thenAccept {
                    if (Coerce.toBoolean(it)) {
                        return@thenAccept
                    }
                    e.isCancelled = true
                }
            }
        }
    }

    @SubscribeEvent
    fun e(e: AdyeshachEntityTickEvent) {
        if (data.contains(e.entity.uniqueId) && Coerce.toLong(e.entity.getTag("view-condition-next") ?: 0) < System.currentTimeMillis()) {
            val script = data.getStringList(e.entity.uniqueId)
            // 设置冷却
            e.entity.setTag("view-condition-next", (System.currentTimeMillis() + (AdyeshachSettings.viewConditionInterval * 50)).toString())
            // 获取玩家
            e.entity.viewPlayers.getPlayersInViewDistance().forEach {
                runKether {
                    KetherShell.eval(script, namespace = listOf("adyeshach"), sender = adaptPlayer(it)).thenAccept { cond ->
                        if (Coerce.toBoolean(cond)) {
                            // 看不见但是满足可视条件
                            if (it.name !in e.entity.viewPlayers.visible) {
                                e.entity.visible(it, true)
                            }
                        } else {
                            // 看得见但不满足可视条件
                            if (it.name in e.entity.viewPlayers.visible) {
                                e.entity.visible(it, false)
                                e.entity.viewPlayers.visible.remove(it.name)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getName(): String {
        return "view-condition"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        player.sendLang("trait-view-condition")
        player.inputBook(data.getStringList(entityInstance.uniqueId)) {
            if (it.all { line -> line.isBlank() }) {
                data[entityInstance.uniqueId] = null
            } else {
                data[entityInstance.uniqueId] = it
            }
            entityInstance.destroy()
            entityInstance.respawn()
            player.sendLang("trait-view-condition-finish")
        }
    }
}