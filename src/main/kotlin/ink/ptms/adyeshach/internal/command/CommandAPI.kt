package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import com.google.gson.GsonBuilder
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.*
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.EntityVillager
import ink.ptms.adyeshach.common.entity.editor.MetaEditor
import ink.ptms.adyeshach.common.entity.type.AdyHorse
import ink.ptms.adyeshach.common.entity.type.AdyPainting
import ink.ptms.adyeshach.common.entity.type.AdyTropicalFish
import ink.ptms.adyeshach.common.util.info
import ink.ptms.adyeshach.common.util.mojang.Model
import ink.ptms.adyeshach.common.util.mojang.MojangAPI
import ink.ptms.adyeshach.internal.migrate.Migrate
import org.bukkit.DyeColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.entity.TropicalFish
import org.bukkit.entity.Villager
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.common5.Mirror
import taboolib.platform.util.sendLang
import java.io.File

@Suppress("UNCHECKED_CAST")
@CommandHeader(name = "adyeshachapi", aliases = ["aapi"], permission = "adyeshach.admin")
internal object CommandAPI {

    @CommandBody
    val verify = subCommand {
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

    @CommandBody
    val mirror = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.sendLang("command-test-mirror-header")
            sender.sendLang("command-test-mirror-bottom")
            submit(async = true) {
                Mirror.report(adaptCommandSender(sender))
                sender.sendLang("command-test-mirror-bottom")
            }
        }
    }

    @CommandBody
    val migrate = subCommand {
        dynamic(commit = "plugin") {
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

    @CommandBody
    val uploadskin = subCommand {
        dynamic(commit = "file") {
            suggestion<CommandSender> { _, _ ->
                File(Adyeshach.plugin.dataFolder, "skin/upload").listFiles()?.map { it.name }?.toList() ?: emptyList()
            }
            dynamic(commit = "model", optional = true) {
                suggestion<CommandSender> { _, _ ->
                    listOf("DEFAULT", "SLIM")
                }
                execute<CommandSender> { sender, context, argument ->
                    commandUploadSign(sender, context.argument(-1)!!, argument)
                }
            }
            execute<CommandSender> { sender, _, argument ->
                commandUploadSign(sender, argument, "DEFAULT")
            }
        }
    }

    @CommandBody
    val edit = subCommand {
        // type
        dynamic(commit = "type") {
            // id
            dynamic(commit = "id") {
                // value
                dynamic(commit = "value") {
                    execute<Player> { sender, context, argument ->
                        val args = argument.split(" ")
                        val entity = AdyeshachAPI.getEntityFromUniqueId(context.argument(-1)!!, sender) ?: return@execute
                        try {
                            when (context.argument(-2)) {
                                "int" -> {
                                    val meta = entity.getEditableEntityMeta().firstOrNull { it.key == args[0] } ?: return@execute
                                    entity.setMetadata(meta.key, Coerce.toInteger(args[1]))
                                    entity.openEditor(sender)
                                }
                                "byte" -> {
                                    val meta = entity.getEditableEntityMeta().firstOrNull { it.key == args[0] } ?: return@execute
                                    entity.setMetadata(meta.key, Coerce.toByte(args[1]))
                                    entity.openEditor(sender)
                                }
                                "text" -> {
                                    val meta = entity.getEditableEntityMeta().firstOrNull { it.key == args[0] } ?: return@execute
                                    entity.setMetadata(meta.key, args[1])
                                    entity.openEditor(sender)
                                }
                                "pose" -> {
                                    entity.setPose(Enums.getIfPresent(BukkitPose::class.java, args[1]).get())
                                    entity.openEditor(sender)
                                }
                                "meta" -> {
                                    val meta = entity.getEditableEntityMeta().firstOrNull { it.key == args[0] } ?: return@execute
                                    val editor = meta.editor ?: return@execute
                                    (editor as MetaEditor<EntityInstance>).modifyMethod?.invoke(sender, entity)
                                }
                                "reset" -> {
                                    val meta = entity.getEditableEntityMeta().firstOrNull { it.key == args[0] } ?: return@execute
                                    if (meta.editor?.resetMethod != null) {
                                        (meta.editor as MetaEditor<EntityInstance>).resetMethod!!.invoke(sender, entity)
                                    } else {
                                        entity.setMetadata(meta.key, meta.def)
                                    }
                                    entity.openEditor(sender)
                                }
                                "particle" -> {
                                    val meta = entity.getEditableEntityMeta().firstOrNull { it.key == args[0] } ?: return@execute
                                    entity.setMetadata(meta.key, Enums.getIfPresent(BukkitParticles::class.java, args[1]).get())
                                    entity.openEditor(sender)
                                }
                                "villager_profession" -> {
                                    val data = (entity as EntityVillager).getVillagerData()
                                    entity.setVillagerData(VillagerData(data.type, Enums.getIfPresent(Villager.Profession::class.java, args[1]).get()))
                                    entity.openEditor(sender)
                                }
                                "villager_profession_legacy" -> {
                                    (entity as EntityVillager).setLegacyProfession(BukkitProfession.valueOf(args[1]))
                                    entity.openEditor(sender)
                                }
                                "painting_painting" -> {
                                    (entity as AdyPainting).setPainting(BukkitPaintings.valueOf(args[1]))
                                    entity.openEditor(sender)
                                }
                                "painting_direction" -> {
                                    (entity as AdyPainting).setDirection(BukkitDirection.valueOf(args[1]))
                                    entity.openEditor(sender)
                                }
                                "pattern_color" -> {
                                    (entity as AdyTropicalFish).setPatternColor(DyeColor.valueOf(args[1]))
                                    entity.openEditor(sender)
                                }
                                "body_color" -> {
                                    (entity as AdyTropicalFish).setBodyColor(DyeColor.valueOf(args[1]))
                                    entity.openEditor(sender)
                                }
                                "pattern" -> {
                                    (entity as AdyTropicalFish).setPattern(TropicalFish.Pattern.valueOf(args[1]))
                                    entity.openEditor(sender)
                                }
                                "horse_color" -> {
                                    (entity as AdyHorse).setColor(Horse.Color.valueOf(args[1]))
                                    entity.openEditor(sender)
                                }
                                "horse_style" -> {
                                    (entity as AdyHorse).setStyle(Horse.Style.valueOf(args[1]))
                                    entity.openEditor(sender)
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

    internal fun commandUploadSign(sender: CommandSender, fileName: String, modelName: String) {
        val model = Enums.getIfPresent(Model::class.java, modelName.uppercase()).or(Model.DEFAULT)
        val file = File(Adyeshach.plugin.dataFolder, "skin/upload/$fileName")
        if (file.exists() && file.name.endsWith(".png")) {
            submit(async = true) {
                sender.sendLang("command-api-skin-header")
                val repose = MojangAPI.upload(file, model, sender)
                if (repose != null) {
                    val value = GsonBuilder().setPrettyPrinting().create().toJson(repose)
                    File(Adyeshach.plugin.dataFolder, "skin/${file.nameWithoutExtension}").writeText(value)
                    sender.sendLang("command-api-skin-success", file.nameWithoutExtension, model)
                }
            }
        } else {
            sender.sendLang("command-api-skin-invalid")
        }
    }
}