package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import com.google.gson.GsonBuilder
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.bukkit.*
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityVillager
import ink.ptms.adyeshach.common.entity.type.AdyHorse
import ink.ptms.adyeshach.common.entity.type.AdyPainting
import ink.ptms.adyeshach.common.entity.type.AdyTropicalFish

import ink.ptms.adyeshach.common.util.mojang.Model
import ink.ptms.adyeshach.common.util.mojang.MojangAPI
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.util.Files
import org.bukkit.DyeColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.entity.TropicalFish
import org.bukkit.entity.Villager
import org.bukkit.util.NumberConversions
import java.io.File
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-05 0:05
 */
@BaseCommand(name = "adyeshachapi", aliases = ["aapi"], permission = "adyeshach.command")
class CommandAPI : BaseMainCommand(), Helper {

    override fun onTabComplete(sender: CommandSender, command: String, argument: String): List<String>? {
        return when (argument) {
            "@command-argument-file" -> File(Adyeshach.plugin.dataFolder, "skin/upload").listFiles()?.map { it.name }?.toList() ?: emptyList()
            "@command-argument-model" -> Model.values().map { it.name }
            else -> null
        }
    }

    @SubCommand(
        description = "@command-api-edit",
        arguments = ["@command-argument-type", "@command-argument-id", "value A?", "value B?"],
        type = CommandType.PLAYER,
        priority = 0.0
    )
    fun edit(sender: Player, args: Array<String>) {
        try {
            when (args[0]) {
                "int" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return
                    val meta = entity.listMetadata().firstOrNull { it.key == args[2] } ?: return
                    entity.setMetadata(meta.key, NumberConversions.toInt(args[3]))
                    Editor.open(sender, entity)
                }
                "byte" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return
                    val meta = entity.listMetadata().firstOrNull { it.key == args[2] } ?: return
                    entity.setMetadata(meta.key, NumberConversions.toByte(args[3]))
                    Editor.open(sender, entity)
                }
                "text" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return
                    val meta = entity.listMetadata().firstOrNull { it.key == args[2] } ?: return
                    entity.setMetadata(meta.key, args[3])
                    Editor.open(sender, entity)
                }
                "pose" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return
                    entity.setPose(Enums.getIfPresent(BukkitPose::class.java, args[3]).get())
                    Editor.open(sender, entity)
                }
                "meta" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return
                    val meta = entity.listMetadata().firstOrNull { it.key == args[2] } ?: return
                    val editor = Editor.getEditor(meta) ?: return
                    editor.onModify?.invoke(sender, entity, meta)
                }
                "reset" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return
                    val meta = entity.listMetadata().firstOrNull { it.key == args[2] } ?: return
                    if (meta.editor?.onReset != null) {
                        meta.editor!!.onReset!!.invoke(entity, meta)
                    } else {
                        entity.setMetadata(meta.key, meta.def)
                    }
                    Editor.open(sender, entity)
                }
                "particle" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return
                    val meta = entity.listMetadata().firstOrNull { it.key == args[2] } ?: return
                    entity.setMetadata(meta.key, Enums.getIfPresent(BukkitParticles::class.java, args[3]).get())
                    Editor.open(sender, entity)
                }
                "villager_type" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return
                    val data = (entity as EntityVillager).getVillagerData()
                    entity.setVillagerData(VillagerData(Enums.getIfPresent(Villager.Type::class.java, args[3]).get(), data.profession))
                    Editor.open(sender, entity)
                }
                "villager_profession" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return
                    val data = (entity as EntityVillager).getVillagerData()
                    entity.setVillagerData(VillagerData(data.type, Enums.getIfPresent(Villager.Profession::class.java, args[3]).get()))
                    Editor.open(sender, entity)
                }
                "villager_profession_legacy" -> {
                    val entity = (AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return) as EntityVillager
                    entity.setLegacyProfession(BukkitProfession.valueOf(args[3]))
                    Editor.open(sender, entity as EntityInstance)
                }
                "painting_painting" -> {
                    val entity = (AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return) as AdyPainting
                    entity.setPainting(BukkitPaintings.valueOf(args[3]))
                    Editor.open(sender, entity)
                }
                "painting_direction" -> {
                    val entity = (AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return) as AdyPainting
                    entity.setDirection(BukkitDirection.valueOf(args[3]))
                    Editor.open(sender, entity)
                }
                "pattern_color" -> {
                    val entity = (AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return) as AdyTropicalFish
                    entity.setPatternColor(DyeColor.valueOf(args[3]))
                    Editor.open(sender, entity)
                }
                "body_color" -> {
                    val entity = (AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return) as AdyTropicalFish
                    entity.setBodyColor(DyeColor.valueOf(args[3]))
                    Editor.open(sender, entity)
                }
                "pattern" -> {
                    val entity = (AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return) as AdyTropicalFish
                    entity.setPattern(TropicalFish.Pattern.valueOf(args[3]))
                    Editor.open(sender, entity)
                }
                "horse_color" -> {
                    val entity = (AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return) as AdyHorse
                    entity.setColor(Horse.Color.valueOf(args[3]))
                    Editor.open(sender, entity)
                }
                "horse_style" -> {
                    val entity = (AdyeshachAPI.getEntityFromUniqueId(args[1], sender) ?: return) as AdyHorse
                    entity.setStyle(Horse.Style.valueOf(args[3]))
                    Editor.open(sender, entity)
                }
                else -> {
                    sender.error("Error type input.")
                }
            }
        } catch (ignore: IndexOutOfBoundsException) {
            sender.error("Error argument input.")
        }
    }

    @SubCommand(description = "@command-api-skin", arguments = ["@command-argument-file", "@command-argument-model"], priority = 0.1)
    fun uploadskin(sender: CommandSender, args: Array<String>) {
        val model = Enums.getIfPresent(Model::class.java, args[1].uppercase(Locale.getDefault())).or(Model.DEFAULT)
        val file = File(Adyeshach.plugin.dataFolder, "skin/upload/${args[0]}")
        if (file.name.endsWith(".png")) {
            submit(async = true) {
                sender.sendLocale("command-api-skin-header")
                val repose = MojangAPI.upload(file, model, sender)
                if (repose != null) {
                    Files.write(File(Adyeshach.plugin.dataFolder, "skin/${args[0].split(".")[0]}")) {
                        it.write(GsonBuilder().setPrettyPrinting().create().toJson(repose))
                    }
                    sender.sendLocale("command-api-skin-success", args[0].split(".")[0], model)
                }
            }
        } else {
            sender.sendLocale("command-api-skin-invalid")
        }
    }
}