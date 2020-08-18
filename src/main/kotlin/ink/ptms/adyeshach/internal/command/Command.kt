package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.Tasks
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
            return arrayOf(Argument("id"), Argument("type") { EntityTypes.values().map { it.name } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entityType = Enums.getIfPresent(EntityTypes::class.java, args[2]).orNull()
            if (entityType == null) {
                sender.error("Entity &f\"${args[2]}\" &7not supported.")
                return
            }
            val entity = try {
                AdyeshachAPI.getEntityManager().create(entityType, (sender as Player).location)
            } catch (t: Throwable) {
                sender.error("Error: &8${t.message}")
                return
            }
            entity.id = args[1]
            sender.info("Adyeshach NPC has been created.")
        }
    }

    @SubCommand(description = "remove adyeshach npc.", type = CommandType.PLAYER)
    val remove = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManager().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManager().getEntity(args[1])
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

    @SubCommand(description = "modify adyeshach npc.", type = CommandType.PLAYER)
    val modify = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManager().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManager().getEntity(args[1])
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