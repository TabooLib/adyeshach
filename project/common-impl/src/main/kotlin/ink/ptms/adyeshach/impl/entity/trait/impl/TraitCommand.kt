package ink.ptms.adyeshach.impl.entity.trait.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.impl.entity.trait.Trait
import ink.ptms.adyeshach.impl.util.Inputs.inputBook
import org.bukkit.entity.Player
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.module.kether.KetherShell
import taboolib.module.kether.runKether
import taboolib.platform.compat.replacePlaceholder
import java.util.concurrent.CompletableFuture

object TraitCommand : Trait() {

    @SubscribeEvent
    private fun onRemove(e: AdyeshachEntityRemoveEvent) {
        data[e.entity.uniqueId] = null
    }

    /**
     * op:say 123
     * op:say 123
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onInteract(e: AdyeshachEntityInteractEvent) {
        if (e.isMainHand && e.entity.uniqueId in data) {
            data.getStringList(e.entity.uniqueId).forEach {
                when {
                    it.startsWith("op:") -> {
                        val isOp = e.player.isOp
                        e.player.isOp = true
                        try {
                            adaptPlayer(e.player).performCommand(it.substringAfter("op:").trim().replace("@player", e.player.name).replacePlaceholder(e.player))
                        } catch (ex: Throwable) {
                            ex.printStackTrace()
                        }
                        e.player.isOp = isOp
                    }
                    it.startsWith("server:") -> {
                        console().performCommand(it.substringAfter("server:").trim().replace("@player", e.player.name).replacePlaceholder(e.player))
                    }
                    it.startsWith("console:") -> {
                        console().performCommand(it.substringAfter("console:").trim().replace("@player", e.player.name).replacePlaceholder(e.player))
                    }
                    it.startsWith("kether:") -> {
                        runKether {
                            KetherShell.eval(it.substringAfter("kether:").trim(), namespace = listOf("adyeshach"), sender = adaptPlayer(e.player)) {
                                set("@entities", e.entity)
                                set("@manager", e.entity.manager)
                            }
                        }
                    }
                    else -> {
                        adaptPlayer(e.player).performCommand(it.replace("@player", e.player.name).replacePlaceholder(e.player))
                    }
                }
            }
        }
    }

    override fun id(): String {
        return "command"
    }

    override fun edit(player: Player, entityInstance: EntityInstance): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        language.sendLang(player, "trait-command")
        player.inputBook(data.getStringList(entityInstance.uniqueId)) {
            entityInstance.setTraitCommands(it)
            future.complete(null)
        }
        return future
    }
}

fun EntityInstance.setTraitCommands(commands: List<String>?) {
    if (commands == null || commands.all { line -> line.isBlank() }) {
        TraitCommand.data[uniqueId] = null
    } else {
        TraitCommand.data[uniqueId] = commands
    }
}

fun EntityInstance.getTraitCommands(): List<String> {
    return TraitCommand.data.getStringList(uniqueId)
}