package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import com.google.gson.GsonBuilder
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.entity.element.VillagerData
import ink.ptms.adyeshach.common.entity.type.AdyVillager
import ink.ptms.adyeshach.common.util.Tasks
import ink.ptms.adyeshach.common.util.mojang.MojangAPI
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.util.Files
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import java.io.File

/**
 * @Author sky
 * @Since 2020-08-05 0:05
 */
@BaseCommand(name = "adyeshachapi", aliases = ["aapi"], permission = "adyeshach.command")
class CommandAPI : BaseMainCommand(), Helper {

    @SubCommand(description = "edit metadata", type = CommandType.PLAYER)
    val edit = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("type"), Argument("uniqueId"), Argument("meta"), Argument("value", false))
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            when (args[0]) {
                "meta" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender as Player) ?: return
                    val meta = entity.listMetadata().firstOrNull { it.key == args[2] } ?: return
                    val editor = Editor.getEditor(meta) ?: return
                    editor.onModify?.invoke(sender, entity, meta)
                }
                "reset" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender as Player) ?: return
                    val meta = entity.listMetadata().firstOrNull { it.key == args[2] } ?: return
                    entity.setMetadata(meta.key, meta.def)
                    Editor.open(sender, entity)
                }
                "particle" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender as Player) ?: return
                    val meta = entity.listMetadata().firstOrNull { it.key == args[2] } ?: return
                    entity.setMetadata(meta.key, Enums.getIfPresent(BukkitParticles::class.java, args[3]).get())
                    Editor.open(sender, entity)
                }
                "villager_type" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender as Player) ?: return
                    val data = (entity as AdyVillager).getVillagerData()
                    entity.setVillagerData(VillagerData(Enums.getIfPresent(Villager.Type::class.java, args[3]).get(), data.profession))
                    Editor.open(sender, entity)
                }
                "villager_profession" -> {
                    val entity = AdyeshachAPI.getEntityFromUniqueId(args[1], sender as Player) ?: return
                    val data = (entity as AdyVillager).getVillagerData()
                    entity.setVillagerData(VillagerData(data.type, Enums.getIfPresent(Villager.Profession::class.java, args[3]).get()))
                    Editor.open(sender, entity)
                }
                "villager_profession_legacy" -> {
                    val entity = (AdyeshachAPI.getEntityFromUniqueId(args[1], sender as Player) ?: return) as AdyVillager
                    entity.setLegacyProfession(BukkitProfession.valueOf(args[3]))
                    Editor.open(sender, entity)
                }
            }
        }
    }

    @SubCommand(description = "upload skin file")
    val uploadskin = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("file") { File(Adyeshach.plugin.dataFolder, "skin/upload").listFiles()?.map { it.name }?.toList() ?: emptyList() })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val file = File(Adyeshach.plugin.dataFolder, "skin/upload/${args[0]}")
            if (file.name.endsWith(".png")) {
                Tasks.task(true) {
                    sender.info("File provided and found, now attempting to upload to mineskin.org.")
                    val repose = MojangAPI.upload(file, sender)
                    if (repose != null) {
                        Files.write(File(Adyeshach.plugin.dataFolder, "skin/${args[0].split(".")[0]}")) {
                            it.write(GsonBuilder().setPrettyPrinting().create().toJson(repose))
                        }
                        sender.info("Successfully uploaded skin and saved as &f\"${args[0].split(".")[0]}\"&7.")
                    }
                }
            } else {
                sender.info("Invalid file provided! Please ensure it is a valid .png skin!")
            }
        }
    }
}