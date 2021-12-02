package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.api.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.Inputs.inputBook
import ink.ptms.adyeshach.internal.trait.Trait
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.util.sendLang

object TraitViewCondition : Trait() {

    @SubscribeEvent
    fun e(e: AdyeshachEntityRemoveEvent) {
        data[e.entity.uniqueId] = null
    }

    @SubscribeEvent
    fun e(e: AdyeshachEntityVisibleEvent) {
        if (e.visible && data.contains(e.entity.uniqueId)) {
            try {
                KetherShell.eval(data.getStringList(e.entity.uniqueId), sender = adaptPlayer(e.viewer)).thenAccept {
                    if (Coerce.toBoolean(it)) {
                        return@thenAccept
                    }
                    e.isCancelled = true
                }
            } catch (ex: Exception) {
                ex.printKetherErrorMessage()
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