package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.Inputs
import ink.ptms.adyeshach.internal.trait.Trait
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Commands
import io.izzel.taboolib.util.Features
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class Command : Trait(), Listener {

    override fun getName(): String {
        return "command"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        player.sendLocale("trait-command")
        Inputs.bookIn(player, data.getStringList(entityInstance.uniqueId)) {
            if (it.all { line -> line.isBlank() }) {
                data.set(entityInstance.uniqueId, null)
            } else {
                data.set(entityInstance.uniqueId, it)
            }
            player.sendLocale("trait-command-finish")
        }
    }

    /**
     * op:say 123
     * op:say 123
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachEntityInteractEvent) {
        if (e.isMainHand && e.entity.uniqueId in data) {
            data.getStringList(e.entity.uniqueId).forEach {
                when {
                    it.startsWith("op:") -> {
                        val command = TLocale.Translate.setPlaceholders(e.player, it.substring("op:".length).replace("@player", e.player.name)).trim()
                        Features.dispatchCommand(e.player, command, true)
                    }
                    it.startsWith("server:") -> {
                        val command = TLocale.Translate.setPlaceholders(e.player, it.substring("server:".length).replace("@player", e.player.name)).trim()
                        Features.dispatchCommand(Bukkit.getConsoleSender(), command)
                    }
                    it.startsWith("console:") -> {
                        val command = TLocale.Translate.setPlaceholders(e.player, it.substring("console:".length).replace("@player", e.player.name)).trim()
                        Features.dispatchCommand(Bukkit.getConsoleSender(), command)
                    }
                    else -> {
                        val command = TLocale.Translate.setPlaceholders(e.player, it.replace("@player", e.player.name)).trim()
                        Features.dispatchCommand(e.player, command)
                    }
                }
            }
        }
    }
}