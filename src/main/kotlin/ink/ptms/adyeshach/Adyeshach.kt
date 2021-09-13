package ink.ptms.adyeshach

import net.minecraft.server.v1_10_R1.PacketPlayOutEntityMetadata
import net.minecraft.server.v1_9_R2.PacketPlayOutNamedEntitySpawn
import taboolib.common.LifeCycle
import taboolib.common.io.newFile
import taboolib.common.platform.*
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.*
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import taboolib.module.lang.sendLang
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketSendEvent
import taboolib.platform.BukkitPlugin
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
            warning("*************************************************************")
            warning(" 你好，感谢使用 Adyeshach (v1.3)!")
            warning(" 版本升级需要进行一些手动迁移工作, 请阅读下方说明进行升级")
            warning(" 若无需迁移可直接删除插件目录并重新启动服务器")
            warning("")
            warning(" 1. 指令以及部分 Kether 脚本的用法存在改动")
            warning(" 2. 基于老版本开发的附属插件将无法支持当前版本")
            warning(" 3. 私有玩家单位管理器下的 NPC 数据将无法继承到当前版本")
            warning("")
            warning(" 确认无误后可在 \"version\" 文件中手动写入 \"1.3\" 完成升级")
            warning("*************************************************************")
            disablePlugin()
        } else {
            newFile(getDataFolder(), "version", create = true).writeText("1.3")
        }
    }

    fun reload() {
        conf.reload()
    }
}
