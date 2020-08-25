package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.move.Picker
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
            val entityType = Enums.getIfPresent(EntityTypes::class.java, args[1]).orNull()
            if (entityType == null) {
                sender.error("Entity &f\"${args[1]}\" &7not supported.")
                return
            }
            val entity = try {
                AdyeshachAPI.getEntityManagerPublic().create(entityType, (sender as Player).location)
            } catch (t: Throwable) {
                t.printStackTrace()
                sender.error("Error: &8${t.message}")
                return
            }
            entity.id = args[0]
            sender.info("Adyeshach NPC has been created.")
        }
    }

    @SubCommand(description = "remove adyeshach npc.", type = CommandType.ALL)
    val remove = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.info("Adyeshach NPC not found.")
                return
            }
            entity.forEach {
                it.delete()
            }
            sender.info("Adyeshach NPC has been removed.")
        }
    }

    @SubCommand(description = "modify adyeshach npc.", type = CommandType.PLAYER)
    val modify = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0]).firstOrNull()
            if (entity == null) {
                sender.info("Adyeshach NPC not found.")
                return
            }
            sender.info("Creating...")
            Editor.open(sender as Player, entity)
        }
    }

    @SubCommand(description = "pickup and move an adyeshach npc.", type = CommandType.PLAYER)
    val move = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0]).firstOrNull()
            if (entity == null) {
                sender.info("Adyeshach NPC not found.")
                return
            }
            sender.info("Picking up...")
            Picker.selecet(sender as Player, entity)
        }
    }

    @SubCommand(description = "save adyeshach npc.")
    val save = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            Tasks.task(true) {
                Bukkit.getOnlinePlayers().forEach {
                    AdyeshachAPI.getEntityManagerPrivate(it).onSave()
                }
                AdyeshachAPI.getEntityManagerPublic().onSave()
                sender.info("Adyeshach NPC has been saved.")
            }
        }
    }

    @SubCommand(description = "reload adyeshach settings.")
    val reload = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            Adyeshach.reload()
            sender.info("Adyeshach Settings has been reloaded.")
        }
    }
}