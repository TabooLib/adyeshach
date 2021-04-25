package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.move.Picker
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.Tasks
import ink.ptms.adyeshach.internal.trait.KnownTraits
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Coerce
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

    @SubCommand(description = "@command-main-create", type = CommandType.PLAYER)
    val create = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id"), Argument("@command-argument-type") { EntityTypes.values().map { it.name } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entityType = Enums.getIfPresent(EntityTypes::class.java, args[1].toUpperCase()).orNull()
            if (entityType == null) {
                sender.sendLocale("command-main-entity-not-support", args[1])
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
            sender.sendLocale("@command-main-entity-create")
            Editor.open(sender, entity)
        }
    }

    @SubCommand(description = "@command-main-delete", type = CommandType.ALL, aliases = ["remove"])
    val delete = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            entity.forEach {
                it.delete()
            }
            sender.sendLocale("command-main-entity-delete")
        }
    }

    @SubCommand(description = "@command-main-modify", type = CommandType.PLAYER, aliases = ["edit"])
    val modify = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = if (args.isEmpty()) {
                AdyeshachAPI.getEntityManagerPublic().getEntities()
            } else {
                AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            }
            if (entity.isEmpty()) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            Editor.open(sender as Player, entity.minBy { it.position.toLocation().toDistance(sender.location) }!!)
        }
    }

    @SubCommand(description = "@command-main-copy", type = CommandType.PLAYER)
    val copy = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } }, Argument("newId"))
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            sender.sendLocale("command-main-success")
            entity.minBy { it.position.toLocation().toDistance((sender as Player).location) }!!.clone(args[1], (sender as Player).location)
        }
    }

    @SubCommand(description = "@command-main-move", type = CommandType.PLAYER)
    val move = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            val entityFirst = entity.minBy { it.position.toLocation().toDistance((sender as Player).location) }!!
            if (entityFirst.getController().isNotEmpty()) {
                sender.sendLocale("command-main-move-cancel")
                return
            }
            sender.sendLocale("command-main-success")
            Picker.select(sender as Player, entityFirst)
        }
    }

    @SubCommand(description = "@command-main-movehere", type = CommandType.PLAYER, aliases = ["tphere"])
    val movehere = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            sender.sendLocale("command-main-success")
            entity.forEach {
                it.teleport((sender as Player).location)
                Tasks.delay(20) {
                    it.setHeadRotation(sender.location.yaw, sender.location.pitch)
                }
            }
        }
    }

    @SubCommand(description = "@command-main-lookhere", type = CommandType.PLAYER)
    val lookhere = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            sender.sendLocale("command-main-success")
            entity.forEach {
                it.controllerLook((sender as Player).eyeLocation)
            }
        }
    }

    @SubCommand(description = "@command-main-teleport", type = CommandType.PLAYER, aliases = ["tp"])
    val teleport = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            sender.sendLocale("command-main-success")
            (sender as Player).teleport(entity.minBy { it.position.toLocation().toDistance(sender.location) }!!.position.toLocation())
        }
    }

    @SubCommand(description = "@command-main-passenger")
    val passenger = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(
                Argument("@command-argument-id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } },
                Argument("method") { listOf("add", "remove", "reset") },
                Argument("id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } }
            )
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            val entityFirst = entity.minBy { it.position.toLocation().toDistance((sender as Player).location) }!!
            when (args[1]) {
                "add" -> {
                    val target = AdyeshachAPI.getEntityManagerPublic().getEntityById(args.getOrNull(2).toString())
                    if (target.isEmpty()) {
                        sender.sendLocale("command-main-entity-not-found")
                        return
                    }
                    val entityTarget = target.minBy { it.position.toLocation().toDistance((sender as Player).location) }!!
                    if (entityFirst == entityTarget) {
                        sender.sendLocale("command-main-passenger-cancel")
                        return
                    }
                    entityFirst.addPassenger(entityTarget)
                    sender.sendLocale("command-main-success")
                }
                "remove" -> {
                    entityFirst.removePassenger(args.getOrNull(2).toString())
                    sender.sendLocale("command-main-success")
                }
                "reset" -> {
                    entityFirst.clearPassengers()
                    sender.sendLocale("command-main-success")
                }
                else -> {
                    sender.sendLocale("command-main-passenger-method-error", args[1])
                }
            }
        }
    }

    @SubCommand(description = "@command-main-controller")
    val controller = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(
                Argument("@command-argument-id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } },
                Argument("@command-argument-method") { listOf("add", "remove", "reset") },
                Argument("@command-argument-name", false) { Adyeshach.scriptHandler.knownControllers.keys().toList() }
            )
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            when (args[1]) {
                "add" -> {
                    val controller = Adyeshach.scriptHandler.getKnownController(args[2])
                    if (controller == null) {
                        sender.sendLocale("command-main-controller-not-found", args[2])
                        return
                    }
                    entity.forEach {
                        it.registerController(controller.get(it))
                    }
                    sender.sendLocale("command-main-success")
                }
                "remove" -> {
                    val controller = Adyeshach.scriptHandler.getKnownController(args[2])
                    if (controller == null) {
                        sender.sendLocale("command-main-controller-not-found", args[2])
                        return
                    }
                    entity.forEach {
                        it.unregisterController(controller.controllerClass)
                    }
                    sender.sendLocale("command-main-success")
                }
                "reset" -> {
                    entity.forEach {
                        it.resetController()
                    }
                    sender.sendLocale("command-main-success")
                }
                else -> {
                    sender.sendLocale("command-main-controller-method-error", args[1])
                }
            }
        }
    }

    @SubCommand(description = "@command-main-trait", type = CommandType.PLAYER)
    var trait: BaseSubCommand = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(
                Argument("@command-argument-id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } },
                Argument("@command-argument-trait") { KnownTraits.traits.map { it.getName() } }
            )
        }

        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityManagerPublic().getEntityById(args[0])
            if (entity.isEmpty()) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            val trait = KnownTraits.traits.firstOrNull { it.getName().equals(args[1], true) }
            if (trait == null) {
                sender.sendLocale("command-main-trait-not-found", args[1])
                return
            }
            trait.edit(sender as Player, entity.minBy { it.position.toLocation().toDistance(sender.location) }!!)
        }
    }

    @SubCommand(description = "@command-main-near", type = CommandType.PLAYER)
    val near = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            sender.sendLocale("command-main-near-header")
            mapOf(
                TLocale.asString("command-main-near-1") to AdyeshachAPI.getEntityManagerPublic(),
                TLocale.asString("command-main-near-2") to AdyeshachAPI.getEntityManagerPublicTemporary(),
                TLocale.asString("command-main-near-3") to AdyeshachAPI.getEntityManagerPrivate(sender as Player),
                TLocale.asString("command-main-near-4") to AdyeshachAPI.getEntityManagerPrivateTemporary(sender),
            ).forEach { (k, v) ->
                v.getEntities().mapNotNull {
                    if (it.getWorld().name == sender.world.name && it.getLocation().distance(sender.location) < 64) {
                        it to it.getLocation().distance(sender.location)
                    } else {
                        null
                    }
                }.sortedBy {
                    it.second
                }.also { result ->
                    if (result.isNotEmpty()) {
                        sender.info("  &f$k:")
                        result.forEach {
                            sender.info("  &8- &7${it.first.id} &a(${Coerce.format(it.second)}m)")
                        }
                    }
                }
            }
        }
    }

    @SubCommand(description = "@command-main-load")
    val load = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            Tasks.task(true) {
                Bukkit.getOnlinePlayers().forEach {
                    AdyeshachAPI.getEntityManagerPrivate(it).onEnable()
                }
                AdyeshachAPI.getEntityManagerPublic().onEnable()
                sender.sendMessage("command-main-success")
            }
        }
    }

    @SubCommand(description = "@command-main-save")
    val save = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            Tasks.task(true) {
                Bukkit.getOnlinePlayers().forEach {
                    AdyeshachAPI.getEntityManagerPrivate(it).onSave()
                }
                AdyeshachAPI.getEntityManagerPublic().onSave()
                sender.sendLocale("command-main-success")
            }
        }
    }

    @SubCommand(description = "@command-main-reload")
    val reload = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            Adyeshach.reload()
            sender.sendLocale("command-main-success")
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