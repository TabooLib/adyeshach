package ink.ptms.adyeshach

import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.*
import taboolib.module.chat.TellrawJson
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import taboolib.module.lang.sendLang
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketSendEvent
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.sendBook
import java.io.File

object Adyeshach : Plugin() {

    @Config(migrate = true)
    lateinit var conf: SecuredFile
        private set

    val plugin by lazy {
        BukkitPlugin.getInstance()
    }

    override fun onLoad() {
        if (MinecraftVersion.majorLegacy < 10900 || !MinecraftVersion.isSupported) {
            console().sendLang("not-support")
            disablePlugin()
        }
    }

    @Awake(LifeCycle.INIT)
    fun checkVersion() {
        if (File(getDataFolder(), "npc").listFiles()?.isNotEmpty() == true) {
            if (newFile(getDataFolder(), "version", create = true).readText() == "1.3") {
                return
            }
            warning("**************************************************************************")
            warning(" 你好，感谢使用 Adyeshach (v1.3)!")
            warning(" 版本升级需要进行一些手动迁移工作, 请参照 1.3 版本更新文档进行升级")
            warning(" 若无需迁移可直接删除插件目录并重新启动服务器")
            warning("")
            warning(" Hi, thanks for using Adyeshach (v1.3)!")
            warning(" Version update requires some manual migration work")
            warning(" If you need to migrate, please refer to the 1.3 version update document")
            warning(" Or delete the plugin folder and restart the server")
            warning(" ")
            warning(" 版本升级文档 | Version Update Document")
            warning(" http://dwz.tax/14zO")
            warning("**************************************************************************")
        } else {
            newFile(getDataFolder(), "version", create = true).writeText("1.3")
        }
    }

    fun reload() {
        conf.reload()
    }
}
