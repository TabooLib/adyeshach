package ink.ptms.adyeshach

import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject

object Adyeshach : Plugin() {

    @TInject(locale = "Language")
    lateinit var conf: TConfig
        private set

    override fun getTabooLibVersion(): Double = 5.35
}
