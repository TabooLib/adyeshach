package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.Tasks
import ink.ptms.adyeshach.internal.mirror.Mirror
import io.izzel.taboolib.module.command.base.*
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-15 0:32
 */
@BaseCommand(name = "adyeshach", aliases = ["anpc", "npc"], permission = "adyeshach.command")
class Command : BaseMainCommand(), Helper {

    @SubCommand(description = "create adyeshach npc.")
    val create = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("manager") { listOf("PUBLIC", "PRIVATE") }, Argument("id"), Argument("type") { EntityTypes.values().map { it.name } }, Argument("viewer", false))
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val player: Player?
            val manager = when (args[0].toUpperCase()) {
                "PUBLIC" -> {
                    if (sender !is Player) {
                        sender.error("The console cannot use this command.")
                        return
                    }
                    player = sender
                    AdyeshachAPI.getEntityManager()
                }
                "PRIVATE" -> {
                    player = Bukkit.getPlayerExact(args.getOrElse(3) { "CONSOLE" })
                    if (player == null) {
                        sender.error("Player &f\"${args.getOrElse(3) { "CONSOLE" }}\" &7not found.")
                        return
                    }
                    AdyeshachAPI.getEntityManager(player)
                }
                else -> {
                    sender.error("Entity Manager &f\"${args[0]}\" &7not supported.")
                    return
                }
            }
            val entityType = Enums.getIfPresent(EntityTypes::class.java, args[2]).orNull()
            if (entityType == null) {
                sender.error("Entity &f\"${args[2]}\" &7not supported.")
                return
            }
            val entity = try {
                manager.create(entityType, player.location)
            } catch (t: Throwable) {
                sender.error("Error: &8${t.message}")
                return
            }
            entity.id = args[1]
            sender.info("Adyeshach NPC has been created.")
        }
    }

    @SubCommand(description = "remove adyeshach npc.")
    val remove = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("manager") { listOf("PUBLIC", "PRIVATE") }, Argument("id") { AdyeshachAPI.getEntityManager().getEntities().map { it.id } }, Argument("viewer", false))
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val manager = when (args[0].toUpperCase()) {
                "PUBLIC" -> AdyeshachAPI.getEntityManager()
                "PRIVATE" -> {
                    val player = Bukkit.getPlayerExact(args.getOrElse(2) { "CONSOLE" })
                    if (player == null) {
                        sender.error("Player &f\"${args.getOrElse(2) { "CONSOLE" }}\" &7not found.")
                        return
                    }
                    AdyeshachAPI.getEntityManager(player)
                }
                else -> {
                    sender.error("Entity Manager &f\"${args[0]}\" &7not supported.")
                    return
                }
            }
            val entity = manager.getEntity(args[1])
            if (entity.isEmpty()) {
                sender.info("Adyeshach NPC not found.")
                return
            }
            entity.forEach {
                it.destroy()
                it.remove()
            }
            sender.info("Adyeshach NPC has been removed.")
        }
    }

    @SubCommand(description = "save adyeshach npc.")
    val save = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            Tasks.task(true) {
                Bukkit.getOnlinePlayers().forEach {
                    AdyeshachAPI.getEntityManager(it).onSave()
                }
                AdyeshachAPI.getEntityManager().onSave()
                sender.info("Adyeshach NPC has been saved.")
            }
        }
    }
}