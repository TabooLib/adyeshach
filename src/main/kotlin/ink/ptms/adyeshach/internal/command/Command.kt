package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.Picker
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.script.KnownController
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.util.error
import ink.ptms.adyeshach.common.util.info
import ink.ptms.adyeshach.internal.trait.TraitFactory
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.*
import taboolib.common.util.Vector
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.TellrawJson
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.*

@CommandHeader(name = "adyeshach", aliases = ["npc", "anpc"], permission = "adyeshach.admin")
internal object Command {

    @CommandBody
    val create = subCommand {
        dynamic {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            dynamic {
                suggestion<Player> { _, _ ->
                    EntityTypes.values().map { it.name }
                }
                execute<Player> { sender, context, argument ->
                    commandCreate(argument, sender, context.argument(-1)!!)
                }
            }
        }
    }

    @CommandBody
    val remove = subCommand {
        dynamic(optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            execute<CommandSender> { sender, _, argument ->
                commandRemove(AdyeshachAPI.getEntityFromUniqueIdOrId(argument, sender as? Player), sender)
            }
        }
        execute<Player> { sender, _, _ ->
            commandRemove(AdyeshachAPI.getEntityNearly(sender), sender)
        }
    }

    @CommandBody
    val rename = subCommand {
        dynamic(optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            dynamic {
                execute<CommandSender> { sender, context, argument ->
                    commandRename(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1)!!, sender as? Player), sender, argument)
                }
            }
        }
        execute<Player> { sender, _, argument ->
            commandRename(AdyeshachAPI.getEntityNearly(sender), sender, argument)
        }
    }

    @CommandBody
    val edit = subCommand {
        dynamic(optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            execute<Player> { sender, _, argument ->
                commandEdit(AdyeshachAPI.getEntityFromUniqueIdOrId(argument, sender as? Player), sender)
            }
        }
        execute<Player> { sender, _, _ ->
            commandEdit(AdyeshachAPI.getEntityNearly(sender), sender)
        }
    }

    @CommandBody
    val copy = subCommand {
        dynamic(optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            dynamic {
                suggestion<Player>(uncheck = true) { sender, _ ->
                    sender.suggestEntityId()
                }
                execute<Player> { sender, context, argument ->
                    commandCopy(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1)!!, sender as? Player), sender, argument)
                }
                literal("to", optional = true) {
                    dynamicLocation({
                        execute<CommandSender> { sender, context, argument ->
                            commandCopy(
                                AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-5)!!, sender as? Player),
                                sender,
                                context.argument(-4)!!,
                                context.argument(-3)!!,
                                Vector(
                                    Coerce.toDouble(context.argument(-2)),
                                    Coerce.toDouble(context.argument(-1)),
                                    Coerce.toDouble(argument)
                                ),
                                0f,
                                0f
                            )
                        }
                    }, {
                        execute<CommandSender> { sender, context, argument ->
                            commandCopy(
                                AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-7)!!, sender as? Player),
                                sender,
                                context.argument(-6)!!,
                                context.argument(-5)!!,
                                Vector(
                                    Coerce.toDouble(context.argument(-4)),
                                    Coerce.toDouble(context.argument(-3)),
                                    Coerce.toDouble(context.argument(-2))
                                ),
                                Coerce.toFloat(context.argument(-1)),
                                Coerce.toFloat(argument)
                            )
                        }
                    })
                }
            }
        }
        execute<Player> { sender, _, argument ->
            commandCopy(AdyeshachAPI.getEntityNearly(sender), sender, argument)
        }
    }

    @CommandBody
    val move = subCommand {
        dynamic(optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            execute<Player> { sender, _, argument ->
                commandMove(AdyeshachAPI.getEntityFromUniqueIdOrId(argument, sender as? Player), sender)
            }
            literal("to", optional = true) {
                dynamicLocation({
                    execute<CommandSender> { sender, context, argument ->
                        commandMove(
                            AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-4)!!, sender as? Player),
                            sender,
                            context.argument(-3)!!,
                            Vector(
                                Coerce.toDouble(context.argument(-2)),
                                Coerce.toDouble(context.argument(-1)),
                                Coerce.toDouble(argument)
                            ),
                            0f,
                            0f
                        )
                    }
                }, {
                    execute<CommandSender> { sender, context, argument ->
                        commandMove(
                            AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-6)!!, sender as? Player),
                            sender,
                            context.argument(-5)!!,
                            Vector(
                                Coerce.toDouble(context.argument(-4)),
                                Coerce.toDouble(context.argument(-3)),
                                Coerce.toDouble(context.argument(-2))
                            ),
                            Coerce.toFloat(context.argument(-1)),
                            Coerce.toFloat(argument)
                        )
                    }
                })
            }
            literal("here", optional = true) {
                execute<Player> { sender, context, _ ->
                    commandMovehere(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1)!!, sender as? Player), sender)
                }
            }
        }
        execute<Player> { sender, _, _ ->
            commandMove(AdyeshachAPI.getEntityNearly(sender), sender)
        }
    }

    @CommandBody
    val look = subCommand {
        dynamic(optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            literal("to") {
                dynamicLocation({
                    execute<CommandSender> { sender, context, argument ->
                        commandLook(
                            AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-4)!!, sender as? Player),
                            sender,
                            context.argument(-3)!!,
                            Vector(
                                Coerce.toDouble(context.argument(-2)),
                                Coerce.toDouble(context.argument(-1)),
                                Coerce.toDouble(argument)
                            ),
                        )
                    }
                })
            }
            literal("here") {
                execute<Player> { sender, context, _ ->
                    commandLook(
                        AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1)!!, sender as? Player),
                        sender,
                        sender.world.name,
                        sender.location.toProxyLocation().toVector()
                    )
                }
            }
        }
    }

    @CommandBody
    val tp = subCommand {
        dynamic(optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            execute<Player> { sender, _, argument ->
                commandTeleport(AdyeshachAPI.getEntityFromUniqueIdOrId(argument, sender as? Player), sender)
            }
        }
        execute<Player> { sender, _, _ ->
            commandTeleport(AdyeshachAPI.getEntityNearly(sender), sender)
        }
    }

    @CommandBody
    val passenger = subCommand {
        dynamic {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            literal("add") {
                dynamic {
                    suggestion<Player>(uncheck = true) { sender, _ ->
                        sender.suggestEntityId()
                    }
                    execute<CommandSender> { sender, context, argument ->
                        commandPassengerAdd(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2)!!, sender as? Player), sender, argument)
                    }
                }
            }
            literal("remove") {
                dynamic {
                    suggestion<Player>(uncheck = true) { sender, _ ->
                        sender.suggestEntityId()
                    }
                    execute<CommandSender> { sender, context, argument ->
                        commandPassengerRemove(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2)!!, sender as? Player), sender, argument)
                    }
                }
            }
            literal("reset") {
                execute<CommandSender> { sender, context, _ ->
                    commandPassengerReset(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2)!!, sender as? Player), sender)
                }
            }
        }
    }

    @CommandBody
    val controller = subCommand {
        dynamic(optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            execute<Player> { sender, _, argument ->
                commandControllerEditor(AdyeshachAPI.getEntityFromUniqueIdOrId(argument, sender as? Player), sender)
            }
            literal("add", optional = true) {
                dynamic {
                    suggestion<CommandSender> { _, _ ->
                        ScriptHandler.controllers.keys().toList()
                    }
                    execute<CommandSender> { sender, context, argument ->
                        commandControllerAdd(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2)!!, sender as? Player), sender, argument)
                    }
                }
            }
            literal("remove", optional = true) {
                dynamic {
                    suggestion<CommandSender> { _, _ ->
                        ScriptHandler.controllers.keys().toList()
                    }
                    execute<CommandSender> { sender, context, argument ->
                        commandControllerRemove(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2)!!, sender as? Player), sender, argument)
                    }
                }
            }
            literal("reset", optional = true) {
                execute<CommandSender> { sender, context, _ ->
                    commandControllerReset(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2)!!, sender as? Player), sender)
                }
            }
        }
        execute<Player> { sender, _, _ ->
            commandControllerEditor(AdyeshachAPI.getEntityNearly(sender), sender)
        }
    }

    @CommandBody
    val trait = subCommand {
        dynamic {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            dynamic {
                suggestion<Player> { _, _ ->
                    TraitFactory.traits.map { it.getName() }
                }
                execute<Player> { sender, context, argument ->
                    commandTrait(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1)!!, sender as? Player), sender, argument)
                }
            }
        }
    }

    @CommandBody
    val near = subCommand {
        execute<Player> { sender, _, _ ->
            commandNear(sender)
        }
    }

    @CommandBody
    val save = subCommand {
        execute<CommandSender> { sender, _, _ ->
            Bukkit.getOnlinePlayers().forEach {
                AdyeshachAPI.getEntityManagerPrivate(it).onSave()
            }
            AdyeshachAPI.getEntityManagerPublic().onSave()
            sender.sendLang("command-main-success")
        }
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            Adyeshach.reload()
            sender.sendLang("command-main-success")
        }
    }

    internal fun Player.suggestEntityId(): List<String> {
        return AdyeshachAPI.getEntities { it.getWorld().name == world.name }.map { it.id }
    }

    internal fun commandCreate(argument: String, sender: Player, name: String) {
        val entityType = Enums.getIfPresent(EntityTypes::class.java, argument.uppercase()).orNull()
        if (entityType == null) {
            sender.sendLang("command-main-entity-not-support", argument)
            return
        }
        val entity = try {
            AdyeshachAPI.getEntityManagerPublic().create(entityType, sender.location)
        } catch (t: Throwable) {
            t.printStackTrace()
            sender.error("Error: &8${t.message}")
            return
        }
        entity.id = name
        sender.sendLang("command-main-entity-create")
        Editor.open(sender, entity)
    }

    internal fun commandRemove(entity: EntityInstance?, sender: CommandSender) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            entity.delete()
            sender.sendLang("command-main-entity-delete")
        }
    }

    internal fun commandRename(entity: EntityInstance?, sender: CommandSender, name: String) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            entity.id = name
            sender.sendLang("command-main-success")
        }
    }

    internal fun commandEdit(entity: EntityInstance?, sender: Player) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            Editor.open(sender, entity)
        }
    }

    internal fun commandCopy(entity: EntityInstance?, sender: Player, name: String) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            entity.clone(name, sender.location)
            sender.sendLang("command-main-entity-create")
            Editor.open(sender, entity)
        }
    }

    internal fun commandCopy(entity: EntityInstance?, sender: CommandSender, name: String, world: String, pos: Vector, yaw: Float, pitch: Float) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            val bukkitWorld = Bukkit.getWorld(world)
            if (bukkitWorld == null) {
                sender.sendLang("command-main-world-not-found")
            } else {
                entity.clone(name, Location(bukkitWorld, pos.x, pos.y, pos.z, yaw, pitch))
                sender.sendLang("command-main-entity-create")
            }
        }
    }

    internal fun commandMove(entity: EntityInstance?, sender: Player) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            if (entity.getController().isNotEmpty()) {
                sender.sendLang("command-main-move-cancel")
            } else {
                Picker.select(sender, entity)
            }
        }
    }

    internal fun commandMove(entity: EntityInstance?, sender: CommandSender, world: String, pos: Vector, yaw: Float, pitch: Float) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            val bukkitWorld = Bukkit.getWorld(world)
            if (bukkitWorld == null) {
                sender.sendLang("command-main-world-not-found")
            } else {
                entity.teleport(Location(bukkitWorld, pos.x, pos.y, pos.z, yaw, pitch))
                submit(delay = 20) {
                    entity.setHeadRotation(yaw, pitch)
                }
                sender.sendLang("command-main-success")
            }
        }
    }

    internal fun commandMovehere(entity: EntityInstance?, sender: Player) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            entity.teleport(sender.location)
            submit(delay = 20) {
                entity.setHeadRotation(sender.location.yaw, sender.location.pitch)
            }
            sender.sendLang("command-main-success")
        }
    }

    internal fun commandLook(entity: EntityInstance?, sender: CommandSender, world: String, pos: Vector) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            val bukkitWorld = Bukkit.getWorld(world)
            if (bukkitWorld == null) {
                sender.sendLang("command-main-world-not-found")
            } else {
                entity.controllerLook(Location(bukkitWorld, pos.x, pos.y, pos.z))
                sender.sendLang("command-main-success")
            }
        }
    }

    internal fun commandTeleport(entity: EntityInstance?, sender: Player) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            sender.teleport(entity.getLocation())
        }
    }

    internal fun commandPassengerAdd(entity: EntityInstance?, sender: CommandSender, name: String) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            val target = AdyeshachAPI.getEntityFromUniqueIdOrId(name, sender as? Player)
            if (target == null || target.manager != entity.manager) {
                sender.sendLang("command-main-entity-not-found")
                return
            }
            if (entity == target) {
                sender.sendLang("command-main-passenger-cancel")
                return
            }
            entity.addPassenger(target)
            sender.sendLang("command-main-success")
        }
    }

    internal fun commandPassengerRemove(entity: EntityInstance?, sender: CommandSender, name: String) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            entity.removePassenger(name)
            sender.sendLang("command-main-success")
        }
    }

    internal fun commandPassengerReset(entity: EntityInstance?, sender: CommandSender) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            entity.clearPassengers()
            sender.sendLang("command-main-success")
        }
    }

    internal fun commandControllerAdd(entity: EntityInstance?, sender: CommandSender, name: String) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            val controller = ScriptHandler.getKnownController(name)
            if (controller == null) {
                sender.sendLang("command-main-controller-not-found", name)
                return
            }
            entity.registerController(controller.get(entity))
            sender.sendLang("command-main-success")
        }
    }

    internal fun commandControllerRemove(entity: EntityInstance?, sender: CommandSender, name: String) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            val controller = ScriptHandler.getKnownController(name)
            if (controller == null) {
                sender.sendLang("command-main-controller-not-found", name)
                return
            }
            entity.unregisterController(controller.controllerClass)
            sender.sendLang("command-main-success")
        }
    }

    internal fun commandControllerReset(entity: EntityInstance?, sender: CommandSender) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            entity.resetController()
            sender.sendLang("command-main-success")
        }
    }

    internal fun commandControllerEditor(entity: EntityInstance?, sender: Player) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            val slots = HashMap<Int, String>()
            fun build(id: String, controller: KnownController): ItemStack {
                val en = entity.getController(controller.controllerClass) != null
                return buildItem(XMaterial.PAPER) {
                    name = "&7$id ${if (en) "&a&lENABLE" else "&c&lDISABLE"}"
                    lore += "&8CLICK TO SELECT"
                    if (en) {
                        shiny()
                    }
                    colored()
                }
            }
            sender.openMenu<Basic>("Controller") {
                rows(6)
                onBuild { _, inv ->
                    AdyeshachAPI.getKnownController().keys.forEachIndexed { index, id ->
                        slots[inventoryCenterSlots[index]] = id
                        inv.setItem(inventoryCenterSlots[index], build(id, AdyeshachAPI.getKnownController(id)!!))
                    }
                }
                onClick(lock = true) {
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
                }
                onClose {
                    Editor.open(sender, entity)
                }
            }
        }
    }

    internal fun commandTrait(entity: EntityInstance?, sender: Player, name: String) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            val trait = TraitFactory.traits.firstOrNull { it.getName().equals(name, true) }
            if (trait == null) {
                sender.sendLang("command-main-trait-not-found", name)
                return
            }
            trait.edit(sender, entity)
        }
    }

    internal fun commandNear(sender: Player) {
        sender.sendLang("command-main-near-header")
        mapOf(
            sender.asLangText("command-main-near-1") to AdyeshachAPI.getEntityManagerPublic(),
            sender.asLangText("command-main-near-2") to AdyeshachAPI.getEntityManagerPublicTemporary(),
            sender.asLangText("command-main-near-3") to AdyeshachAPI.getEntityManagerPrivate(sender),
            sender.asLangText("command-main-near-4") to AdyeshachAPI.getEntityManagerPrivateTemporary(sender),
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
                        TellrawJson()
                            .append("§c[Adyeshach]  §8- §a${Coerce.format(it.second)}m §7${it.first.id} §8(${it.first.getDisplayName()}§8)")
                            .hoverText("CLICK TO TELEPORT")
                            .runCommand("/anpc tp ${it.first.uniqueId}")
                            .sendTo(adaptCommandSender(sender))
                    }
                }
            }
        }
    }

    internal fun CommandBuilder.CommandComponent.dynamicLocation(
        func1: CommandBuilder.CommandComponentDynamic.() -> Unit,
        func2: (CommandBuilder.CommandComponentDynamic.() -> Unit)? = null,
    ) {
        // world
        dynamic {
            suggestion<CommandSender> { _, _ -> Bukkit.getWorlds().map { it.name } }
            // x
            dynamic {
                restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
                // y
                dynamic {
                    restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
                    // z
                    dynamic {
                        func1(this)
                        if (func2 != null) {
                            // yaw
                            dynamic(optional = true) {
                                restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
                                // pitch
                                dynamic {
                                    restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
                                    func2(this)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}