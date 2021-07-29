package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.Settings
import ink.ptms.adyeshach.common.script.ScriptHandler
import org.bukkit.Bukkit
import taboolib.common.platform.Plugin
import taboolib.common.platform.console
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import taboolib.module.lang.sendLang
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.BukkitPlugin

object Adyeshach : Plugin() {

    @Config(migrate = true)
    lateinit var conf: SecuredFile
        private set

    val plugin = BukkitPlugin.getInstance()

    var settings = Settings
        private set

    val scriptHandler = ScriptHandler

    override fun onLoad() {
        if (MinecraftVersion.majorLegacy < 10900 || !MinecraftVersion.isSupported) {
            console().sendLang("not-support")
            Bukkit.getPluginManager().disablePlugin(plugin)
        }
    }

    fun reload() {
        conf.reload()
    }
}
