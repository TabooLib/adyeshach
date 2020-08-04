package ink.ptms.adyeshach.internal.command

import com.google.gson.GsonBuilder
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.util.Tasks
import ink.ptms.adyeshach.common.util.mojang.MojangAPI
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.util.Files
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.io.File

/**
 * @Author sky
 * @Since 2020-08-05 0:05
 */
@BaseCommand(name = "adyeshachapi", aliases = ["aapi"], permission = "adyeshach.command")
class CommandAPI : BaseMainCommand() {

    @SubCommand(description = "upload skin file")
    val uploadskin = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("file") { File(Adyeshach.plugin.dataFolder, "skin/upload").listFiles()?.map { it.name }?.toList() ?: emptyList() })
        }

        override fun onCommand(sender: CommandSender, p1: Command?, p2: String?, args: Array<String>) {
            val file = File(Adyeshach.plugin.dataFolder, "skin/upload/${args[0]}")
            if (file.name.endsWith(".png")) {
                Tasks.task(true) {
                    sender.sendMessage("§c[Adyeshach] §7File provided and found, now attempting to upload to mineskin.org.")
                    val repose = MojangAPI.upload(file, sender)
                    if (repose != null) {
                        Files.write(File(Adyeshach.plugin.dataFolder, "skin/${args[0].split(".")[0]}")) {
                            it.write(GsonBuilder().setPrettyPrinting().create().toJson(repose))
                        }
                        sender.sendMessage("§c[Adyeshach] §7Successfully uploaded skin and saved as \"§f${args[0].split(".")[0]}§77\".")
                    }
                }
            } else {
                sender.sendMessage("§c[Adyeshach] §7Invalid file provided! Please ensure it is a valid .png skin!")
            }
        }
    }
}