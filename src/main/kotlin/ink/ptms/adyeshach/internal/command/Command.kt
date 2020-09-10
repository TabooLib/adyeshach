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
import org.bukkit.Location
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
            val entityType = Enums.getIfPresent(EntityTypes::class.java, args[1].toUpperCase()).orNull()
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

    @SubCommand(description = "remove adyeshach npc.", type = CommandType.ALL, aliases = ["remove"])
    val delete = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.error("Adyeshach NPC not found.")
                return
            }
            entity.forEach {
                it.delete()
            }
            sender.info("Adyeshach NPC has been removed.")
        }
    }

    @SubCommand(description = "modify adyeshach npc.", type = CommandType.PLAYER, aliases = ["edit"])
    val modify = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.error("Adyeshach NPC not found.")
                return
            }
            sender.info("Creating...")
            Editor.open(sender as Player, entity.minBy { it.position.toLocation().toDistance(sender.location) }!!)
        }
    }

    @SubCommand(description = "copy adyeshach npc.", type = CommandType.PLAYER)
    val copy = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.error("Adyeshach NPC not found.")
                return
            }
            sender.info("Coping...")
            entity.minBy { it.position.toLocation().toDistance((sender as Player).location) }!!.clone(args[0], (sender as Player).location)
        }
    }

    @SubCommand(description = "pickup and move adyeshach npc.", type = CommandType.PLAYER)
    val move = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.error("Adyeshach NPC not found.")
                return
            }
            val entityFirst = entity.minBy { it.position.toLocation().toDistance((sender as Player).location) }!!
            if (entityFirst.getController().isNotEmpty()) {
                sender.error("Please unregister the Adyeshach NPC controller first.")
                return
            }
            sender.info("Picking up...")
            Picker.select(sender as Player, entityFirst)
        }
    }

    @SubCommand(description = "move adyeshach npc.", type = CommandType.PLAYER, aliases = ["tphere"])
    val movehere = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.error("Adyeshach NPC not found.")
                return
            }
            sender.info("Moving...")
            entity.forEach {
                it.teleport((sender as Player).location)
                it.setHeadRotation(sender.location.yaw, sender.location.pitch)
            }
        }
    }

    @SubCommand(description = "teleport to adyeshach npc.", type = CommandType.PLAYER, aliases = ["tp"])
    val teleport = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.error("Adyeshach NPC not found.")
                return
            }
            sender.info("Teleport...")
            (sender as Player).teleport(entity.minBy { it.position.toLocation().toDistance(sender.location) }!!.position.toLocation())
        }
    }

    @SubCommand(description = "modify passenger of adyeshach npc.")
    val passenger = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(
                    Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } },
                    Argument("method") { listOf("add", "remove", "reset") },
                    Argument("id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } }
            )
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.error("Adyeshach NPC not found.")
                return
            }
            val entityFirst = entity.minBy { it.position.toLocation().toDistance((sender as Player).location) }!!
            when (args[1]) {
                "add" -> {
                    val target = AdyeshachAPI.getEntityManagerPublic().getEntityById(args.getOrNull(2).toString())
                    if (target.isEmpty()) {
                        sender.error("Adyeshach NPC not found.")
                        return
                    }
                    val entityTarget = target.minBy { it.position.toLocation().toDistance((sender as Player).location) }!!
                    if (entityFirst == entityTarget) {
                        sender.error("Please choose different Adyeshach NPC.")
                        return
                    }
                    entityFirst.addPassenger(entityTarget)
                    sender.info("Changed.")
                }
                "remove" -> {
                    entityFirst.removePassenger(args.getOrNull(2).toString())
                    sender.info("Changed.")
                }
                "reset" -> {
                    entityFirst.clearPassengers()
                    sender.info("Changed.")
                }
                else -> {
                    sender.error("Unknown passenger method ${args[1]} (add,remove,reset)")
                }
            }
        }
    }

    @SubCommand(description = "modify controller of adyeshach npc.")
    val controller = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(
                    Argument("id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } },
                    Argument("method") { listOf("add", "remove", "reset") },
                    Argument("name") { Adyeshach.scriptHandler.knownControllers.keys().toList() }
            )
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.error("Adyeshach NPC not found.")
                return
            }
            when (args[1]) {
                "add" -> {
                    val controller = Adyeshach.scriptHandler.getKnownController(args[2])
                    if (controller == null) {
                        sender.error("Unknown controller ${args[2]}")
                        return
                    }
                    entity.forEach {
                        it.registerController(controller.get.invoke(it))
                    }
                    sender.info("Changed.")
                }
                "remove" -> {
                    val controller = Adyeshach.scriptHandler.getKnownController(args[2])
                    if (controller == null) {
                        sender.error("Unknown controller ${args[2]}")
                        return
                    }
                    entity.forEach {
                        it.unregisterController(controller.controllerClass)
                    }
                    sender.info("Changed.")
                }
                "reset" -> {
                    entity.forEach {
                        it.resetController()
                    }
                    sender.info("Changed.")
                }
                else -> {
                    sender.error("Unknown controller method ${args[1]} (add,remove,reset)")
                }
            }
        }
    }

    @SubCommand(description = "load adyeshach npc.")
    val load = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            Tasks.task(true) {
                Bukkit.getOnlinePlayers().forEach {
                    AdyeshachAPI.getEntityManagerPrivate(it).onEnable()
                }
                AdyeshachAPI.getEntityManagerPublic().onEnable()
                sender.info("Adyeshach NPC has been loaded.")
            }
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

    fun Location.toDistance(loc: Location): Double {
        return if (this.world!!.name == loc.world!!.name) {
            this.distance(loc)
        } else {
            Double.MAX_VALUE
        }
    }
}