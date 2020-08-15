package ink.ptms.adyeshach

import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.loader.PluginRedefine
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject

@Plugin.Version(5.34) // The TabooLib dependency version (required)
object Adyeshach : PluginRedefine() {

    @TInject(locale = "Language")
    lateinit var conf: TConfig
        private set

}
