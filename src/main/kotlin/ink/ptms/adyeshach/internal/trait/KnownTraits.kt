package ink.ptms.adyeshach.internal.trait

import ink.ptms.adyeshach.Adyeshach
import io.izzel.taboolib.TabooLibLoader
import io.izzel.taboolib.module.inject.TFunction

object KnownTraits {

    val traits = ArrayList<Trait>()

    @TFunction.Init
    fun init() {
        TabooLibLoader.getPluginClassSafely(Adyeshach.plugin).forEach {
            if (Trait::class.java.isAssignableFrom(it) && Trait::class.java != it) {
                try {
                    traits.add(it.newInstance() as Trait)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
    }
}