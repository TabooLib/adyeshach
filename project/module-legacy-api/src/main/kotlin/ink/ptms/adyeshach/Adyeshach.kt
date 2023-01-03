package ink.ptms.adyeshach

import ink.ptms.adyeshach.core.AdyeshachSettings
import taboolib.common.util.ResettableLazy
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin

@Deprecated("Outdated but usable")
object Adyeshach {

    var conf = AdyeshachSettings.conf

    var editorConf = Configuration.empty()

    val plugin by lazy {
        BukkitPlugin.getInstance()
    }

    fun reload() {
        AdyeshachSettings.conf.reload()
        ResettableLazy.reset()
    }
}
