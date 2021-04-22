package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.Inputs
import ink.ptms.adyeshach.internal.trait.Trait
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Commands
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
        player.sendLocale("trait-command-title")
        Inputs.bookIn(player) {
            if (it.all { line -> line.isBlank() }) {
                data.set(entityInstance.uniqueId, null)
            } else {
                data.set(entityInstance.uniqueId, it)
            }
            player.sendLocale("trait-command-finish")
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: AdyeshachEntityInteractEvent) {
        if (e.isMainHand && e.entity.uniqueId in data) {
            data.getStringList(e.entity.uniqueId).forEach {
                when {
                    it.startsWith("op:") -> {
                        val isOp = e.player.isOp
                        e.player.isOp = true
                        try {
                            Commands.dispatchCommand(e.player, TLocale.Translate.setPlaceholders(e.player, it.substring("op:".length).replace("@player", e.player.name)))
                        } catch (t: Throwable) {
                            t.printStackTrace()
                        }
                        e.player.isOp = isOp
                    }
                    it.startsWith("server:") -> {
                        Commands.dispatchCommand(Bukkit.getConsoleSender(), TLocale.Translate.setPlaceholders(e.player, it.substring("server:".length).replace("@player", e.player.name)))
                    }
                    else -> {
                        Commands.dispatchCommand(e.player, TLocale.Translate.setPlaceholders(e.player, it.replace("@player", e.player.name)))
                    }
                }
            }
        }
    }
}