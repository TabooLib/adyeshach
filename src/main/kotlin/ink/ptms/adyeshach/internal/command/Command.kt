package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.*
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.EntityVillager
import ink.ptms.adyeshach.common.entity.type.AdyHorse
import ink.ptms.adyeshach.common.entity.type.AdyPainting
import ink.ptms.adyeshach.common.entity.type.AdyTropicalFish
import ink.ptms.adyeshach.common.util.info
import ink.ptms.adyeshach.common.util.mojang.Model
import ink.ptms.adyeshach.internal.migrate.Migrate
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.entity.TropicalFish
import org.bukkit.entity.Villager
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.adaptCommandSender
import taboolib.common.platform.command
import taboolib.common.platform.submit
import taboolib.common.util.Vector
import taboolib.common5.Coerce
import taboolib.common5.Mirror
import taboolib.platform.util.sendLang
import taboolib.platform.util.toProxyLocation
import java.io.File

object Command {

    @Awake(LifeCycle.ENABLE)
    fun register() {
        command("adyeshach", aliases = listOf("npc", "anpc"), permission = "adyeshach.admin") {
            // npc create [npc] [type]
            literal("create") {
                dynamic {
                    suggestion<Player>(uncheck = true) { sender, _ ->
                        sender.suggestEntityId()
                    }
                    dynamic {
                        suggestion<Player> { _, _ ->
                            EntityTypes.values().map { it.name }
                        }
                        execute<Player> { sender, context, argument ->
                            commandCreate(argument, sender, context.argument(-1))
                        }
                    }
                }
            }
            // npc remove [npc]
            literal("remove") {
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
            // npc rename [npc] [newId]
            literal("rename") {
                dynamic(optional = true) {
                    suggestion<Player>(uncheck = true) { sender, _ ->
                        sender.suggestEntityId()
                    }
                    dynamic {
                        execute<CommandSender> { sender, context, argument ->
                            commandRename(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1), sender as? Player), sender, argument)
                        }
                    }
                }
                execute<Player> { sender, _, argument ->
                    commandRename(AdyeshachAPI.getEntityNearly(sender), sender, argument)
                }
            }
            // npc edit [npc]
            literal("edit") {
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
            // npc copy [npc] [newId] (to [world] [x] [y] [z] ([yaw] [pitch]))
            literal("copy") {
                dynamic(optional = true) {
                    suggestion<Player>(uncheck = true) { sender, _ ->
                        sender.suggestEntityId()
                    }
                    dynamic {
                        suggestion<Player>(uncheck = true) { sender, _ ->
                            sender.suggestEntityId()
                        }
                        execute<Player> { sender, context, argument ->
                            commandCopy(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1), sender as? Player), sender, argument)
                        }
                        literal("to", optional = true) {
                            dynamicLocation({
                                execute<CommandSender> { sender, context, argument ->
                                    commandCopy(
                                        AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-5), sender as? Player),
                                        sender,
                                        context.argument(-4),
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
                                        AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-7), sender as? Player),
                                        sender,
                                        context.argument(-6),
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
                execute<Player> { sender, _, argument ->
                    commandCopy(AdyeshachAPI.getEntityNearly(sender), sender, argument)
                }
            }
            // npc move [npc] (to [world] [x] [y] [z] ([yaw] [pitch]))
            literal("move") {
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
                                    AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-4), sender as? Player),
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
                                    AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-6), sender as? Player),
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
                    literal("here", optional = true) {
                        execute<Player> { sender, context, _ ->
                            commandMovehere(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1), sender as? Player), sender)
                        }
                    }
                }
                execute<Player> { sender, _, _ ->
                    commandMove(AdyeshachAPI.getEntityNearly(sender), sender)
                }
            }
            // npc look [npc] (to [world] [x] [y] [z])
            literal("look") {
                dynamic(optional = true) {
                    suggestion<Player>(uncheck = true) { sender, _ ->
                        sender.suggestEntityId()
                    }
                    literal("to") {
                        dynamicLocation({
                            execute<CommandSender> { sender, context, argument ->
                                commandLook(
                                    AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-4), sender as? Player),
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
                                sender.location.toProxyLocation().toVector()
                            )
                        }
                    }
                }
            }
            // npc teleport [npc]
            literal("tp") {
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
            // npc passenger [npc] (add|remove|reset) [id]
            literal("passenger") {
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
                                commandPassengerAdd(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-2), sender as? Player), sender, argument)
                            }
                        }
                    }
                    literal("remove") {
                        dynamic {
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
            // npc controller [npc] ((add|remove|reset) [controller])
            literal("controller") {
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
                                Adyeshach.scriptHandler.controllers.keys().toList()
                            }
                            execute<CommandSender> { sender, context, argument ->
                                commandControllerAdd(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1), sender as? Player), sender, argument)
                            }
                        }
                    }
                    literal("remove", optional = true) {
                        dynamic {
                            suggestion<CommandSender> { _, _ ->
                                Adyeshach.scriptHandler.controllers.keys().toList()
                            }
                            execute<CommandSender> { sender, context, argument ->
                                commandControllerRemove(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1), sender as? Player), sender, argument)
                            }
                        }
                    }
                    literal("reset", optional = true) {
                        execute<CommandSender> { sender, context, _ ->
                            commandControllerReset(AdyeshachAPI.getEntityFromUniqueIdOrId(context.argument(-1), sender as? Player), sender)
                        }
                    }
                }
                execute<Player> { sender, _, _ ->
                    commandControllerEditor(AdyeshachAPI.getEntityNearly(sender), sender)
                }
            }
            // npc trait [npc]
            literal("trait") {
                dynamic {
                    suggestion<Player>(uncheck = true) { sender, _ ->
                        sender.suggestEntityId()
                    }
                    execute<Player> { sender, _, argument ->
                        commandTrait(AdyeshachAPI.getEntityFromUniqueIdOrId(argument, sender as? Player), sender, argument)
                    }
                }
            }
            // npc near
            literal("near") {
                execute<Player> { sender, _, _ ->
                    commandNear(sender)
                }
            }
            // npc save
            literal("save") {
                execute<CommandSender> { sender, _, _ ->
                    Bukkit.getOnlinePlayers().forEach {
                        AdyeshachAPI.getEntityManagerPrivate(it).onSave()
                    }
                    AdyeshachAPI.getEntityManagerPublic().onSave()
                    sender.sendLang("command-main-success")
                }
            }
            // npc reload
            literal("reload") {
                execute<CommandSender> { sender, _, _ ->
                    Adyeshach.reload()
                    sender.sendLang("command-main-success")
                }
            }
        }
        command("adyeshachapi", aliases = listOf("aapi"), permission = "adyeshach.admin") {
            literal("edit") {
                // type
                dynamic {
                    // id
                    dynamic {
                        // value
                        dynamic {
                            execute<Player> { sender, context, argument ->
                                val args = argument.split(" ")
                                val entity = AdyeshachAPI.getEntityFromUniqueId(context.argument(-1), sender) ?: return@execute
                                try {
                                    when (context.argument(-2)) {
                                        "int" -> {
                                            val meta = entity.listMetadata().firstOrNull { it.key == args[0] } ?: return@execute
                                            entity.setMetadata(meta.key, Coerce.toInteger(args[1]))
                                            Editor.open(sender, entity)
                                        }
                                        "byte" -> {
                                            val meta = entity.listMetadata().firstOrNull { it.key == args[0] } ?: return@execute
                                            entity.setMetadata(meta.key, Coerce.toByte(args[1]))
                                            Editor.open(sender, entity)
                                        }
                                        "text" -> {
                                            val meta = entity.listMetadata().firstOrNull { it.key == args[0] } ?: return@execute
                                            entity.setMetadata(meta.key, args[1])
                                            Editor.open(sender, entity)
                                        }
                                        "pose" -> {
                                            entity.setPose(Enums.getIfPresent(BukkitPose::class.java, args[1]).get())
                                            Editor.open(sender, entity)
                                        }
                                        "meta" -> {
                                            val meta = entity.listMetadata().firstOrNull { it.key == args[0] } ?: return@execute
                                            val editor = Editor.getEditor(meta) ?: return@execute
                                            editor.onModify?.invoke(sender, entity, meta)
                                        }
                                        "reset" -> {
                                            val meta = entity.listMetadata().firstOrNull { it.key == args[0] } ?: return@execute
                                            if (meta.editor?.onReset != null) {
                                                meta.editor!!.onReset!!.invoke(entity, meta)
                                            } else {
                                                entity.setMetadata(meta.key, meta.def)
                                            }
                                            Editor.open(sender, entity)
                                        }
                                        "particle" -> {
                                            val meta = entity.listMetadata().firstOrNull { it.key == args[0] } ?: return@execute
                                            entity.setMetadata(meta.key, Enums.getIfPresent(BukkitParticles::class.java, args[1]).get())
                                            Editor.open(sender, entity)
                                        }
                                        "villager_profession" -> {
                                            val data = (entity as EntityVillager).getVillagerData()
                                            entity.setVillagerData(VillagerData(data.type, Enums.getIfPresent(Villager.Profession::class.java, args[1]).get()))
                                            Editor.open(sender, entity)
                                        }
                                        "villager_profession_legacy" -> {
                                            (entity as EntityVillager).setLegacyProfession(BukkitProfession.valueOf(args[1]))
                                            Editor.open(sender, entity)
                                        }
                                        "painting_painting" -> {
                                            (entity as AdyPainting).setPainting(BukkitPaintings.valueOf(args[1]))
                                            Editor.open(sender, entity)
                                        }
                                        "painting_direction" -> {
                                            (entity as AdyPainting).setDirection(BukkitDirection.valueOf(args[1]))
                                            Editor.open(sender, entity)
                                        }
                                        "pattern_color" -> {
                                            (entity as AdyTropicalFish).setPatternColor(DyeColor.valueOf(args[1]))
                                            Editor.open(sender, entity)
                                        }
                                        "body_color" -> {
                                            (entity as AdyTropicalFish).setBodyColor(DyeColor.valueOf(args[1]))
                                            Editor.open(sender, entity)
                                        }
                                        "pattern" -> {
                                            (entity as AdyTropicalFish).setPattern(TropicalFish.Pattern.valueOf(args[1]))
                                            Editor.open(sender, entity)
                                        }
                                        "horse_color" -> {
                                            (entity as AdyHorse).setColor(Horse.Color.valueOf(args[1]))
                                            Editor.open(sender, entity)
                                        }
                                        "horse_style" -> {
                                            (entity as AdyHorse).setStyle(Horse.Style.valueOf(args[1]))
                                            Editor.open(sender, entity)
                                        }
                                    }
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
            literal("verify") {
                execute<CommandSender> { sender, _, _ ->
                    sender.sendLang("command-test-verify-header")
                    EntityTypes.values().forEach {
                        try {
                            NMS.INSTANCE.getEntityTypeNMS(it)
                            sender.info("  §f$it &aSUPPORTED")
                        } catch (t: Throwable) {
                            sender.info("  §f$it &cERROR")
                        }
                    }
                }
            }
            literal("mirror") {
                execute<CommandSender> { sender, _, _ ->
                    sender.sendLang("command-test-mirror-header")
                    sender.sendLang("command-test-mirror-bottom")
                    submit(async = true) {
                        Mirror.report(adaptCommandSender(sender))
                        sender.sendLang("command-test-mirror-bottom")
                    }
                }
            }
            literal("migrate") {
                dynamic {
                    suggestion<CommandSender> { _, _ ->
                        Migrate.migrates.filter { it.value.isEnabled() }.map { it.key }.toList()
                    }
                    execute<CommandSender> { sender, _, argument ->
                        val migrate = Migrate.migrates[argument]
                        if (migrate == null || !migrate.isEnabled()) {
                            sender.sendLang("command-test-migrate-not-support", argument)
                        } else {
                            val time = System.currentTimeMillis()
                            sender.sendLang("command-test-migrate-header")
                            migrate.migrate()
                            sender.sendLang("command-test-migrate-bottom", System.currentTimeMillis() - time)
                        }
                    }
                }
            }
            literal("uploadskin") {
                dynamic {
                    suggestion<CommandSender> { _, _ ->
                        File(Adyeshach.plugin.dataFolder, "skin/upload").listFiles()?.map { it.name }?.toList() ?: emptyList()
                    }
                    dynamic(optional = true) {
                        suggestion<CommandSender> { _, _ ->
                            listOf("DEFAULT", "SLIM")
                        }
                        execute<CommandSender> { sender, context, argument ->
                            commandUploadSign(sender, context.argument(-1), argument)
                        }
                    }
                    execute<CommandSender> { sender, context, argument ->
                        commandUploadSign(sender, argument, "DEFAULT")
                    }
                }
            }
        }
        command("adyeshachscript", aliases = listOf("ascript"), permission = "adyeshach.admin") {
            literal("run") {
                // name
                dynamic {
                    // viewer
                    dynamic {
                        // vars
                        dynamic {

                        }
                    }
                }
            }
        }
    }
}