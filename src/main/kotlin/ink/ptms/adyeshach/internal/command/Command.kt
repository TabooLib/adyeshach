package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.move.Picker
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.script.KnownController
import ink.ptms.adyeshach.common.util.Tasks
import ink.ptms.adyeshach.internal.trait.KnownTraits
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.Coerce
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

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
            return arrayOf(Argument("@command-argument-id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = if (args.isEmpty()) {
                AdyeshachAPI.getEntityNearly(sender as Player)
            } else {
                AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
            }
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            entity.delete()
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
                AdyeshachAPI.getEntityNearly(sender as Player)
            } else {
                AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
            }
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            Editor.open(sender, entity)
        }
    }

    @SubCommand(description = "@command-main-copy", type = CommandType.PLAYER)
    val copy = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } }, Argument("newId"))
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = if (args.isEmpty()) {
                AdyeshachAPI.getEntityNearly(sender as Player)
            } else {
                AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
            }
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            sender.sendLocale("command-main-success")
            entity.clone(args[1], sender.location)
        }
    }

    @SubCommand(description = "@command-main-move", type = CommandType.PLAYER)
    val move = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = if (args.isEmpty()) {
                AdyeshachAPI.getEntityNearly(sender as Player)
            } else {
                AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
            }
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            if (entity.getController().isNotEmpty()) {
                sender.sendLocale("command-main-move-cancel")
                return
            }
            sender.sendLocale("command-main-success")
            Picker.select(sender, entity)
        }
    }

    @SubCommand(description = "@command-main-movehere", type = CommandType.PLAYER, aliases = ["tphere"])
    val movehere = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = if (args.isEmpty()) {
                AdyeshachAPI.getEntityNearly(sender as Player)
            } else {
                AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
            }
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            sender.sendLocale("command-main-success")
            entity.teleport(sender.location)
            Tasks.delay(20) {
                entity.setHeadRotation(sender.location.yaw, sender.location.pitch)
            }
        }
    }

    @SubCommand(description = "@command-main-lookhere", type = CommandType.PLAYER)
    val lookhere = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = if (args.isEmpty()) {
                AdyeshachAPI.getEntityNearly(sender as Player)
            } else {
                AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
            }
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            sender.sendLocale("command-main-success")
            entity.controllerLook(sender.eyeLocation)
        }
    }

    @SubCommand(description = "@command-main-teleport", type = CommandType.PLAYER, aliases = ["tp"])
    val teleport = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("@command-argument-id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } })
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = if (args.isEmpty()) {
                AdyeshachAPI.getEntityNearly(sender as Player)
            } else {
                AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
            }
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            sender.sendLocale("command-main-success")
            sender.teleport(entity.getLocation())
        }
    }

    @SubCommand(description = "@command-main-passenger")
    val passenger = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(
                Argument("@command-argument-id") { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } },
                Argument("@command-argument-method") { listOf("add", "remove", "reset") },
                Argument("@command-argument-id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } }
            )
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            val entity = AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            when (args[1]) {
                "add" -> {
                    val target = AdyeshachAPI.getEntityFromUniqueIdOrId(args.getOrNull(2).toString())
                    if (target == null || target.manager != entity.manager) {
                        sender.sendLocale("command-main-entity-not-found")
                        return
                    }
                    if (entity == target) {
                        sender.sendLocale("command-main-passenger-cancel")
                        return
                    }
                    entity.addPassenger(target)
                    sender.sendLocale("command-main-success")
                }
                "remove" -> {
                    entity.removePassenger(args.getOrNull(2).toString())
                    sender.sendLocale("command-main-success")
                }
                "reset" -> {
                    entity.clearPassengers()
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
                Argument("@command-argument-id", false) { AdyeshachAPI.getEntityManagerPublic().getEntities().map { it.id } },
                Argument("@command-argument-method", false) { listOf("add", "remove", "reset") },
                Argument("@command-argument-name", false) { Adyeshach.scriptHandler.knownControllers.keys().toList() }
            )
        }

        override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>) {
            if (args.size < 2) {
                val entity = if (args.isEmpty()) {
                    AdyeshachAPI.getEntityNearly(sender as Player)
                } else {
                    AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
                }
                if (entity == null) {
                    sender.sendLocale("command-main-entity-not-found")
                    return
                }
                val slots = HashMap<Int, String>()
                fun build(id: String, controller: KnownController): ItemStack {
                    val en = entity.getController(controller.controllerClass) != null
                    return ItemBuilder(Material.PAPER)
                        .name("&7$id ${if (en) "&a&lENABLE" else "&c&lDISABLE"}")
                        .lore("&8CLICK TO SELECT")
                        .also {
                            if (en) {
                                it.shiny()
                            }
                        }.colored().build()
                }
                MenuBuilder.builder(Adyeshach.plugin)
                    .title("Controllers")
                    .rows(6)
                    .build { inv ->
                        AdyeshachAPI.getKnownController().keys.forEachIndexed { index, id ->
                            slots[Items.INVENTORY_CENTER[index]] = id
                            inv.setItem(Items.INVENTORY_CENTER[index], build(id, AdyeshachAPI.getKnownController(id)!!))
                        }
                    }.click {
                        if (slots.containsKey(it.rawSlot)) {
                            val controller = AdyeshachAPI.getKnownController(slots[it.rawSlot]!!)!!
                            if (entity.getController(controller.controllerClass) == null) {
                                entity.registerController(controller.get(entity))
                                it.inventory.setItem(it.rawSlot, build(slots[it.rawSlot]!!, controller))
                            } else {
                                entity.unregisterController(controller.controllerClass)
                                it.inventory.setItem(it.rawSlot, build(slots[it.rawSlot]!!, controller))
                            }
                            sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
                        }
                    }.open(sender)
            } else {
                val entity = AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
                if (entity == null) {
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
                        entity.registerController(controller.get(entity))
                        sender.sendLocale("command-main-success")
                    }
                    "remove" -> {
                        val controller = Adyeshach.scriptHandler.getKnownController(args[2])
                        if (controller == null) {
                            sender.sendLocale("command-main-controller-not-found", args[2])
                            return
                        }
                        entity.unregisterController(controller.controllerClass)
                        sender.sendLocale("command-main-success")
                    }
                    "reset" -> {
                        entity.resetController()
                        sender.sendLocale("command-main-success")
                    }
                    else -> {
                        sender.sendLocale("command-main-controller-method-error", args[1])
                    }
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
            val entity = AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender as Player)
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            val trait = KnownTraits.traits.firstOrNull { it.getName().equals(args[1], true) }
            if (trait == null) {
                sender.sendLocale("command-main-trait-not-found", args[1])
                return
            }
            trait.edit(sender, entity)
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
                            TellrawJson.create()
                                .append("  &8- &a${Coerce.format(it.second)}m &7${it.first.id} &8(${it.first.getDisplayName()}&8)")
                                .hoverText("CLICK TO TELEPORT")
                                .clickCommand("/anpc tp ${it.first.uniqueId}")
                                .send(sender)
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
                    AdyeshachAPI.getEntityManagerPrivate(it).onDisable()
                    AdyeshachAPI.getEntityManagerPrivate(it).onEnable()
                }
                AdyeshachAPI.getEntityManagerPublic().onDisable()
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
}