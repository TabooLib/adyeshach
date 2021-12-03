package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.ControllerGenerator
import ink.ptms.adyeshach.common.entity.editor.EditorPicker
import ink.ptms.adyeshach.common.entity.editor.toLocaleKey
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
import org.bukkit.util.Vector
import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.expansion.createHelper
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.parseToXMaterial
import taboolib.module.chat.TellrawJson
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.buildItem
import taboolib.platform.util.inventoryCenterSlots
import taboolib.platform.util.sendLang

@CommandHeader(name = "adyeshach", aliases = ["npc", "anpc"], permission = "adyeshach.admin")
internal object Command {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val api = CommandAPI

    @CommandBody
    val script = CommandScript

    @CommandBody
    val create = subCommand {
        dynamic(commit = "id") {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            dynamic(commit = "type") {
                suggestion<Player> { _, _ ->
                    EntityTypes.values().map { it.name }
                }
                execute<Player> { sender, context, argument ->
                    commandCreate(argument, sender, context.argument(-1))
                }
            }
        }
    }

    @CommandBody
    val remove = subCommand {
        dynamic(commit = "id", optional = true) {
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
        dynamic(commit = "id") {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            execute<Player> { sender, _, argument ->
                commandRename(AdyeshachAPI.getEntityNearly(sender), sender, argument)
            }
            dynamic(commit = "new id", optional = true) {
                execute<CommandSender> { sender, context, argument ->
                    commandRename(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1), sender as? Player), sender, argument)
                }
            }
        }
    }

    @CommandBody
    val edit = subCommand {
        dynamic(commit = "id", optional = true) {
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
        dynamic(commit = "id") {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            execute<Player> { sender, _, argument ->
                commandCopy(AdyeshachAPI.getEntityNearly(sender), sender, argument)
            }
            dynamic(commit = "new id", optional = true) {
                suggestion<Player>(uncheck = true) { sender, _ ->
                    sender.suggestEntityId()
                }
                literal("here") {
                    execute<Player> { sender, context, _ ->
                        commandCopy(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2), sender as? Player), sender, context.argument(-1))
                    }
                }
                literal("to") {
                    dynamicLocation({
                        execute<CommandSender> { sender, context, argument ->
                            commandCopy(
                                AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-6), sender as? Player),
                                sender,
                                context.argument(-5),
                                context.argument(-3),
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
                                AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-8), sender as? Player),
                                sender,
                                context.argument(-7),
                                context.argument(-5),
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
    }

    @CommandBody
    val move = subCommand {
        dynamic(commit = "id", optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            execute<Player> { sender, _, argument ->
                commandMove(AdyeshachAPI.getEntityFromUniqueIdOrId(argument, sender as? Player), sender)
            }
            literal("here", optional = true) {
                execute<Player> { sender, context, _ ->
                    commandMovehere(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1), sender as? Player), sender)
                }
            }
            literal("to", optional = true) {
                dynamicLocation({
                    execute<CommandSender> { sender, context, argument ->
                        commandMove(
                            AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-5), sender as? Player),
                            sender,
                            context.argument(-3),
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
                            AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-7), sender as? Player),
                            sender,
                            context.argument(-5),
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
        execute<Player> { sender, _, _ ->
            commandMove(AdyeshachAPI.getEntityNearly(sender), sender)
        }
    }

    @CommandBody
    val look = subCommand {
        dynamic(commit = "id", optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            literal("to") {
                dynamicLocation({
                    execute<CommandSender> { sender, context, argument ->
                        commandLook(
                            AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-5), sender as? Player),
                            sender,
                            context.argument(-3),
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
                        AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1), sender as? Player),
                        sender,
                        sender.world.name,
                        sender.eyeLocation.toVector()
                    )
                }
            }
        }
    }

    @CommandBody
    val tp = subCommand {
        dynamic(commit = "id", optional = true) {
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
        dynamic(commit = "id") {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            literal("add") {
                dynamic(commit = "id") {
                    suggestion<Player>(uncheck = true) { sender, _ ->
                        sender.suggestEntityId()
                    }
                    execute<CommandSender> { sender, context, argument ->
                        commandPassengerAdd(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2), sender as? Player), sender, argument)
                    }
                }
            }
            literal("remove") {
                dynamic(commit = "id") {
                    suggestion<Player>(uncheck = true) { sender, _ ->
                        sender.suggestEntityId()
                    }
                    execute<CommandSender> { sender, context, argument ->
                        commandPassengerRemove(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2), sender as? Player), sender, argument)
                    }
                }
            }
            literal("reset") {
                execute<CommandSender> { sender, context, _ ->
                    commandPassengerReset(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2), sender as? Player), sender)
                }
            }
        }
    }

    @CommandBody
    val controller = subCommand {
        dynamic(commit = "id", optional = true) {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            execute<Player> { sender, _, argument ->
                commandControllerEditor(AdyeshachAPI.getEntityFromUniqueIdOrId(argument, sender as? Player), sender)
            }
            literal("add", optional = true) {
                dynamic(commit = "id") {
                    suggestion<CommandSender> { _, _ ->
                        AdyeshachAPI.registeredControllerGenerator.keys.toList()
                    }
                    execute<CommandSender> { sender, context, argument ->
                        commandControllerAdd(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2), sender as? Player), sender, argument)
                    }
                }
            }
            literal("remove", optional = true) {
                dynamic(commit = "id") {
                    suggestion<CommandSender> { _, _ ->
                        AdyeshachAPI.registeredControllerGenerator.keys.toList()
                    }
                    execute<CommandSender> { sender, context, argument ->
                        commandControllerRemove(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2), sender as? Player), sender, argument)
                    }
                }
            }
            literal("reset", optional = true) {
                execute<CommandSender> { sender, context, _ ->
                    commandControllerReset(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2), sender as? Player), sender)
                }
            }
        }
        execute<Player> { sender, _, _ ->
            commandControllerEditor(AdyeshachAPI.getEntityNearly(sender), sender)
        }
    }

    @CommandBody
    val trait = subCommand {
        dynamic(commit = "id") {
            suggestion<Player>(uncheck = true) { sender, _ ->
                sender.suggestEntityId()
            }
            dynamic(commit = "trait") {
                suggestion<Player> { _, _ ->
                    TraitFactory.traits.map { it.getName() }
                }
                execute<Player> { sender, context, argument ->
                    commandTrait(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1), sender as? Player), sender, argument)
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
        entity.openEditor(sender)
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
            entity.openEditor(sender)
        }
    }

    internal fun commandCopy(entity: EntityInstance?, sender: Player, name: String) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            entity.clone(name, sender.location)
            sender.sendLang("command-main-entity-create")
            entity.openEditor(sender)
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
                EditorPicker.select(sender, entity)
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
                submit(delay = 5) {
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
            submit(delay = 5) {
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
            val controller = ScriptHandler.getControllerGenerator(name)
            if (controller == null) {
                sender.sendLang("command-main-controller-not-found", name)
                return
            }
            entity.registerController(controller.generator.apply(entity))
            sender.sendLang("command-main-success")
        }
    }

    internal fun commandControllerRemove(entity: EntityInstance?, sender: CommandSender, name: String) {
        if (entity == null) {
            sender.sendLang("command-main-entity-not-found")
        } else {
            val controller = ScriptHandler.getControllerGenerator(name)
            if (controller == null) {
                sender.sendLang("command-main-controller-not-found", name)
                return
            }
            entity.unregisterController(controller.type)
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
            fun build(id: String, controller: ControllerGenerator): ItemStack {
                val key = id.toLocaleKey().substring(1)
                val enable = entity.getController(controller.type) != null
                return buildItem(Adyeshach.editorConf.getString("Materials.controller.$key")?.parseToXMaterial() ?: XMaterial.PAPER) {
                    val display = sender.asLangText("editor-controller-$key")
                    name = "&7$display ${sender.asLangText("editor-controller-${if (enable) "enable" else "disable"}")}"
                    lore += sender.asLangText("editor-select")
                    if (enable) {
                        shiny()
                    }
                    hideAll()
                    colored()
                }
            }
            sender.openMenu<Basic>(sender.asLangText("editor-controller")) {
                rows(6)
                onBuild { _, inv ->
                    AdyeshachAPI.registeredControllerGenerator.keys.forEachIndexed { index, id ->
                        slots[inventoryCenterSlots[index]] = id
                        inv.setItem(inventoryCenterSlots[index], build(id, AdyeshachAPI.getControllerGenerator(id)!!))
                    }
                }
                onClick(lock = true) {
                    if (slots.containsKey(it.rawSlot)) {
                        val controller = AdyeshachAPI.getControllerGenerator(slots[it.rawSlot]!!)!!
                        if (entity.getController(controller.type) == null) {
                            entity.registerController(controller.generator.apply(entity))
                            it.inventory.setItem(it.rawSlot, build(slots[it.rawSlot]!!, controller))
                        } else {
                            entity.unregisterController(controller.type)
                            it.inventory.setItem(it.rawSlot, build(slots[it.rawSlot]!!, controller))
                        }
                        sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
                    }
                }
                onClose {
                    entity.openEditor(sender)
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
                            .hoverText(sender.asLangText("editor-select-teleport"))
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
        dynamic(commit = "world") {
            suggestion<CommandSender> { _, _ -> Bukkit.getWorlds().map { it.name } }
            // x
            dynamic(commit = "x") {
                restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
                // y
                dynamic(commit = "y") {
                    restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
                    // z
                    dynamic(commit = "z") {
                        func1(this)
                        if (func2 != null) {
                            // yaw
                            dynamic(commit = "yaw", optional = true) {
                                restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
                                // pitch
                                dynamic(commit = "pitch") {
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