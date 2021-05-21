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
import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.BaseMainCommand
import io.izzel.taboolib.module.command.base.CommandType
import io.izzel.taboolib.module.command.base.SubCommand
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.Coerce
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @author sky
 * @since 2020-08-15 0:32
 */
@BaseCommand(name = "adyeshach", aliases = ["anpc", "npc"], permission = "adyeshach.command")
class Command : BaseMainCommand(), Helper {

    override fun onTabComplete(sender: CommandSender, command: String, argument: String): List<String>? {
        return when (argument) {
            "@command-argument-id" -> if (sender is Player) AdyeshachAPI.getEntities { it.getWorld().name == sender.world.name }.map { it.id } else null
            "@command-argument-type" -> EntityTypes.values().map { it.name }
            "@command-argument-method" -> listOf("add", "remove", "reset")
            "@command-argument-controller" -> Adyeshach.scriptHandler.knownControllers.keys().toList()
            "@command-argument-trait" -> KnownTraits.traits.map { it.getName() }
            else -> null
        }
    }

    @SubCommand(description = "@command-main-create", arguments = ["@command-argument-id", "@command-argument-type"], type = CommandType.PLAYER, priority = 0.1)
    fun create(sender: Player, args: Array<String>) {
        val entityType = Enums.getIfPresent(EntityTypes::class.java, args[1].toUpperCase()).orNull()
        if (entityType == null) {
            sender.sendLocale("command-main-entity-not-support", args[1])
            return
        }
        val entity = try {
            AdyeshachAPI.getEntityManagerPublic().create(entityType, sender.location)
        } catch (t: Throwable) {
            t.printStackTrace()
            sender.error("Error: &8${t.message}")
            return
        }
        entity.id = args[0]
        sender.sendLocale("@command-main-entity-create")
        Editor.open(sender, entity)
    }

    @SubCommand(description = "@command-main-delete", arguments = ["@command-argument-id?"], aliases = ["remove"], type = CommandType.PLAYER, priority = 0.2)
    fun delete(sender: Player, args: Array<String>) {
        val entity = if (args.isEmpty()) {
            AdyeshachAPI.getEntityNearly(sender)
        } else {
            AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
        }
        if (entity == null) {
            sender.sendLocale("command-main-entity-not-found")
            return
        }
        entity.delete()
        sender.sendLocale("command-main-entity-delete")
    }

    @SubCommand(description = "@command-main-rename", arguments = ["@command-argument-id", "@command-argument-id?"], priority = 0.21)
    fun rename(sender: Player, args: Array<String>) {
        if (args.size == 1) {
            val entity = AdyeshachAPI.getEntityNearly(sender)
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            entity.id = args[0]
            sender.sendLocale("command-main-success")
        } else {
            val entity = AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
            if (entity == null) {
                sender.sendLocale("command-main-entity-not-found")
                return
            }
            entity.id = args[1]
            sender.sendLocale("command-main-success")
        }
    }

    @SubCommand(description = "@command-main-modify", arguments = ["@command-argument-id?"], aliases = ["edit"], type = CommandType.PLAYER, priority = 0.3)
    fun modify(sender: Player, args: Array<String>) {
        val entity = if (args.isEmpty()) {
            AdyeshachAPI.getEntityNearly(sender)
        } else {
            AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
        }
        if (entity == null) {
            sender.sendLocale("command-main-entity-not-found")
            return
        }
        Editor.open(sender, entity)
    }

    @SubCommand(description = "@command-main-copy", arguments = ["@command-argument-id", "@command-argument-id"], type = CommandType.PLAYER, priority = 0.4)
    fun copy(sender: Player, args: Array<String>) {
        val entity = AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
        if (entity == null) {
            sender.sendLocale("command-main-entity-not-found")
            return
        }
        sender.sendLocale("command-main-success")
        entity.clone(args[1], sender.location)
    }

    @SubCommand(description = "@command-main-move", arguments = ["@command-argument-id?"], type = CommandType.PLAYER, priority = 0.5)
    fun move(sender: Player, args: Array<String>) {
        val entity = if (args.isEmpty()) {
            AdyeshachAPI.getEntityNearly(sender)
        } else {
            AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
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

    @SubCommand(description = "@command-main-movehere", arguments = ["@command-argument-id?"], aliases = ["tphere"], type = CommandType.PLAYER, priority = 0.6)
    fun movehere(sender: Player, args: Array<String>) {
        val entity = if (args.isEmpty()) {
            AdyeshachAPI.getEntityNearly(sender)
        } else {
            AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
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

    @SubCommand(description = "@command-main-lookhere", arguments = ["@command-argument-id?"], type = CommandType.PLAYER, priority = 0.7)
    fun lookhere(sender: Player, args: Array<String>) {
        val entity = if (args.isEmpty()) {
            AdyeshachAPI.getEntityNearly(sender)
        } else {
            AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
        }
        if (entity == null) {
            sender.sendLocale("command-main-entity-not-found")
            return
        }
        sender.sendLocale("command-main-success")
        entity.controllerLook(sender.eyeLocation)
    }

    @SubCommand(description = "@command-main-teleport", arguments = ["@command-argument-id?"], aliases = ["tp"], type = CommandType.PLAYER, priority = 0.8)
    fun teleport(sender: Player, args: Array<String>) {
        val entity = if (args.isEmpty()) {
            AdyeshachAPI.getEntityNearly(sender)
        } else {
            AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
        }
        if (entity == null) {
            sender.sendLocale("command-main-entity-not-found")
            return
        }
        sender.sendLocale("command-main-success")
        sender.teleport(entity.getLocation())
    }

    @SubCommand(
        description = "@command-main-passenger",
        arguments = ["@command-argument-id", "@command-argument-method", "@command-argument-id?"],
        type = CommandType.PLAYER,
        priority = 0.9
    )
    fun passenger(sender: Player, args: Array<String>) {
        val entity = AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
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

    @SubCommand(
        description = "@command-main-controller",
        arguments = ["@command-argument-id?", "@command-argument-method?", "@command-argument-controller?"],
        type = CommandType.PLAYER,
        priority = 1.0
    )
    fun controller(sender: Player, args: Array<String>) {
        if (args.size < 2) {
            val entity = if (args.isEmpty()) {
                AdyeshachAPI.getEntityNearly(sender)
            } else {
                AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
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
                }.close {
                    Editor.open(sender, entity)
                }.open(sender)
        } else if (args.size == 3) {
            val entity = AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
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
        } else {
            sender.sendLocale("command-main-controller-no-argument")
        }
    }

    @SubCommand(
        description = "@command-main-trait",
        arguments = ["@command-argument-id", "@command-argument-trait"],
        type = CommandType.PLAYER,
        priority = 1.1
    )
    fun trait(sender: Player, args: Array<String>) {
        val entity = AdyeshachAPI.getEntityFromUniqueIdOrId(args[0], sender)
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

    @SubCommand(description = "@command-main-near", type = CommandType.PLAYER, priority = 1.2)
    fun near(sender: Player, args: Array<String>) {
        sender.sendLocale("command-main-near-header")
        mapOf(
            TLocale.asString("command-main-near-1") to AdyeshachAPI.getEntityManagerPublic(),
            TLocale.asString("command-main-near-2") to AdyeshachAPI.getEntityManagerPublicTemporary(),
            TLocale.asString("command-main-near-3") to AdyeshachAPI.getEntityManagerPrivate(sender),
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
                    sender.info("  §f$k:")
                    result.forEach {
                        TellrawJson.create()
                            .append("§c[Adyeshach]  §8- §a${Coerce.format(it.second)}m §7${it.first.id} §8(${it.first.getDisplayName()}§8)")
                            .hoverText("CLICK TO TELEPORT")
                            .clickCommand("/anpc tp ${it.first.uniqueId}")
                            .send(sender)
                    }
                }
            }
        }
    }

    @SubCommand(description = "@command-main-load", priority = 1.3)
    fun load(sender: CommandSender, args: Array<String>) {
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

    @SubCommand(description = "@command-main-save", priority = 1.4)
    fun save(sender: CommandSender, args: Array<String>) {
        Tasks.task(true) {
            Bukkit.getOnlinePlayers().forEach {
                AdyeshachAPI.getEntityManagerPrivate(it).onSave()
            }
            AdyeshachAPI.getEntityManagerPublic().onSave()
            sender.sendLocale("command-main-success")
        }
    }

    @SubCommand(description = "@command-main-reload", priority = 1.5)
    fun reload(sender: CommandSender, args: Array<String>) {
        Adyeshach.reload()
        sender.sendLocale("command-main-success")
    }
}