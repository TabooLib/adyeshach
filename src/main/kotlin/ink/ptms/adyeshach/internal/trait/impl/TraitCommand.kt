package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.Inputs.inputBook
import ink.ptms.adyeshach.internal.trait.Trait
import org.bukkit.entity.Player
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.sendLang

object TraitCommand : Trait() {

    @SubscribeEvent
    fun e(e: AdyeshachEntityRemoveEvent) {
        data[e.entity.uniqueId] = null
    }

    /**
     * op:say 123
     * op:say 123
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachEntityInteractEvent) {
        if (e.isMainHand && e.entity.uniqueId in data) {
            data.getStringList(e.entity.uniqueId).forEach {
                when {
                    it.startsWith("op:") -> {
                        val isOp = e.player.isOp
                        e.player.isOp = true
                        try {
                            adaptPlayer(e.player).performCommand(it.substring("op:".length).replace("@player", e.player.name).replacePlaceholder(e.player))
                        } catch (ex: Throwable) {
                            ex.printStackTrace()
                        }
                        e.player.isOp = isOp
                    }
                    it.startsWith("server:") -> {
                        console().performCommand(it.substring("server:".length).replace("@player", e.player.name).replacePlaceholder(e.player))
                    }
                    it.startsWith("console:") -> {
                        console().performCommand(it.substring("console:".length).replace("@player", e.player.name).replacePlaceholder(e.player))
                    }
                    else -> {
                        adaptPlayer(e.player).performCommand(it.replace("@player", e.player.name).replacePlaceholder(e.player))
                    }
                }
            }
        }
    }

    override fun getName(): String {
        return "command"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        player.sendLang("trait-command")
        player.inputBook(data.getStringList(entityInstance.uniqueId)) {
            if (it.all { line -> line.isBlank() }) {
                data[entityInstance.uniqueId] = null
            } else {
                data[entityInstance.uniqueId] = it
            }
            player.sendLang("trait-command-finish")
        }
    }
}