package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
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

    @SubCommand(description = "create adyeshach npc.", type = CommandType.PLAYER)
    val create = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("manager") { listOf("PUBLIC", "PRIVATE") }, Argument("id"), Argument("type") { EntityTypes.values().map { it.name } }, Argument("viewer", false))
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val manager = when (args[0].toUpperCase()) {
                "PUBLIC" -> AdyeshachAPI.getEntityManager()
                "PRIVATE" -> {
                    val player = Bukkit.getPlayerExact(args.getOrElse(3) { "CONSOLE" })
                    if (player == null) {
                        sender.error("Player \"${args[3]}\" not found.")
                        return
                    }
                    AdyeshachAPI.getEntityManager(player)
                }
                else -> {
                    sender.error("Entity Manager \"${args[0]}\" not supported.")
                    return
                }
            }
            val entityType = Enums.getIfPresent(EntityTypes::class.java, args[2]).orNull()
            if (entityType == null) {
                sender.error("Entity \"${args[2]}\" not supported.")
                return
            }
            val entity = try {
                manager.create(entityType, (sender as Player).location)
            } catch (t: Throwable) {
                sender.error("Error: &8${t.message}")
                return
            }
            entity.id = args[1]
        }
    }

    @SubCommand(description = "remove adyeshach npc.", type = CommandType.PLAYER)
    val remove = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("manager") { listOf("PUBLIC", "PRIVATE") }, Argument("id"), Argument("viewer", false))
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val manager = when (args[0].toUpperCase()) {
                "PUBLIC" -> AdyeshachAPI.getEntityManager()
                "PRIVATE" -> AdyeshachAPI.getEntityManager(sender as Player)
                else -> {
                    sender.error("Entity Manager \"${args[0]}\" not supported.")
                    return
                }
            }
        }
    }
}