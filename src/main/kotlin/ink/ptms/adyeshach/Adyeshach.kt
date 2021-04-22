package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.Settings
import ink.ptms.adyeshach.common.script.ScriptHandler
import io.izzel.taboolib.Version
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.loader.PluginBoot
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.Bukkit

object Adyeshach : Plugin() {

    @TInject(migrate = true)
    lateinit var conf: TConfig
        private set

    var settings = Settings()
        private set

    val scriptHandler = ScriptHandler

    override fun onLoad() {
        if (PluginBoot.isForge() || Version.isBefore(Version.v1_9)) {
            PluginBoot.setDisabled(true)
            Bukkit.getConsoleSender().sendLocale("not-support")
            try {
                CommandBuilder.create("adyeshach", plugin)
                        .aliases("anpc", "npc")
                        .execute { sender, _ ->
                            sender.sendLocale("not-support")
                        }.build()
            } catch (t: Throwable) {
            }
        }
    }

    fun reload() {
        settings = Settings()
    }
}
