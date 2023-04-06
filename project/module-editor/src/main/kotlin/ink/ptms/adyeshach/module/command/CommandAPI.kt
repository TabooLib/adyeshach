package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.core.ADYESHACH_PREFIX
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachNetworkAPI
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.module.editor.ChatEditor
import ink.ptms.adyeshach.module.editor.meta.impl.MetaPrimitive
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submitAsync
import taboolib.expansion.createHelper
import taboolib.platform.util.hasMeta
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta
import java.io.File

@CommandHeader(name = "adyeshachapi", aliases = ["aapi"], permission = "adyeshach.command")
object CommandAPI {

    var uploading = false

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    /**
     * 静默输入
     */
    @CommandBody
    val se = subCommand {
        dynamic("command") {
            execute<Player> { sender, _, args ->
                sender.setMeta("adyeshach_ignore_notice", true)
                try {
                    adaptPlayer(sender).performCommand(args)
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }
                sender.removeMeta("adyeshach_ignore_notice")
            }
        }
    }

    /**
     * 静默输入并刷新页面
     */
    @CommandBody
    val ee = subCommand {
        dynamic("command") {
            execute<Player> { sender, _, args ->
                sender.setMeta("adyeshach_ignore_notice", true)
                try {
                    adaptPlayer(sender).performCommand(args)
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }
                ChatEditor.refresh(sender)
                sender.removeMeta("adyeshach_ignore_notice")
            }
        }
    }

    /**
     * 输入设置
     */
    @CommandBody
    val pi = subCommand {
        dynamic("input-type") {
            suggest { listOf("SIGN", "CHAT") }
            execute<Player> { sender, _, args ->
                MetaPrimitive.setPreferenceInputType(sender, MetaPrimitive.InputType.valueOf(args))
                ChatEditor.refresh(sender)
            }
        }
    }

    /**
     * 迁移
     */
    @CommandBody
    val migrate = subCommand {
        dynamic("plugin") {
            suggest { listOf("citizens") }
            execute<Player> { sender, _, args ->
                when (args) {
                    "citizens" -> {
                        MigrateCitizens.migrate(sender)
                    }
                }
            }
        }
    }

    /**
     * 皮肤上传
     */
    @CommandBody
    val uploadSkin = subCommand {
        dynamic("file") {
            suggestUncheck { File(getDataFolder(), "skin/upload").listFiles()?.map { it.name } ?: emptyList() }
            dynamic("model") {
                suggest { listOf("DEFAULT", "SLIM") }
                execute<CommandSender> { sender, ctx, _ ->
                    uploadSkin(sender, ctx["file"], AdyeshachNetworkAPI.SkinModel.valueOf(ctx["model"].uppercase()))
                }
            }
            execute<CommandSender> { sender, ctx, _ ->
                uploadSkin(sender, ctx["file"], AdyeshachNetworkAPI.SkinModel.DEFAULT)
            }
        }
    }

    /**
     * 检查
     */
    @CommandBody
    val verify = subCommand {
        execute<CommandSender> { sender, _, args ->
            EntityType.values().forEach { type ->
                try {
                    Adyeshach.api().getEntityTypeRegistry().getEntityTypeFromBukkit(type)
                    sender.sendMessage("${ADYESHACH_PREFIX}Entity type §f\"${type.name}\"§7 is§a SUPPORTED")
                } catch (ex: Throwable) {
                    sender.sendMessage("${ADYESHACH_PREFIX}Entity type §f\"${type.name}\"§7 is§c UNSUPPORTED")
                }
            }
        }
    }

    /**
     * 调试模式
     */
    @CommandBody
    val debug = subCommand {
        execute<CommandSender> { sender, _, _ ->
            AdyeshachSettings.debug = !AdyeshachSettings.debug
            sender.sendMessage("${ADYESHACH_PREFIX}Debug mode is now ${if (AdyeshachSettings.debug) "§aENABLED" else "§cDISABLED"}")
        }
    }

    /**
     * 刷新
     */
    @CommandBody
    val refresh = subCommand {
        execute<Player> { sender, _, _ ->
            Adyeshach.api().refreshEntityManager(sender)
            sender.sendMessage("${ADYESHACH_PREFIX}Refreshed.")
        }
    }

    fun uploadSkin(sender: CommandSender, name: String, model: AdyeshachNetworkAPI.SkinModel) {
        if (uploading) {
            sender.sendMessage("${ADYESHACH_PREFIX}Currently uploading, please wait.")
            return
        }
        val file = File(getDataFolder(), "skin/upload/$name")
        if (file.exists()) {
            sender.sendMessage("${ADYESHACH_PREFIX}Skin §f\"${file.nameWithoutExtension} (DEFAULT)\"§7 is uploading...")
            submitAsync {
                uploading = true
                try {
                    val uploadTexture = Adyeshach.api().getNetworkAPI().getSkin().uploadTexture(file, model, sender)
                    if (uploadTexture != null) {
                        newFile(getDataFolder(), "skin/${file.nameWithoutExtension}").writeText(uploadTexture.toString())
                        sender.sendMessage("${ADYESHACH_PREFIX}Skin §f\"${file.nameWithoutExtension} (DEFAULT)\"§7 has been uploaded.")
                    }
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                } finally {
                    uploading = false
                }
            }
        } else {
            sender.sendMessage("${ADYESHACH_PREFIX}Skin §f\"${file.name}\"§7 not found.")
        }
    }
}

fun CommandSender.isIgnoreNotice(): Boolean {
    return this is Player && hasMeta("adyeshach_ignore_notice")
}