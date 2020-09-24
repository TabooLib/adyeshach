package ink.ptms.adyeshach.internal.trait

import ink.ptms.adyeshach.Adyeshach
import io.izzel.taboolib.TabooLibLoader
import io.izzel.taboolib.module.inject.TFunction
import org.bukkit.Bukkit
import org.bukkit.event.Listener

object KnownTraits {

    val traits = ArrayList<Trait>()

    @TFunction.Init
    fun init() {
        TabooLibLoader.getPluginClassSafely(Adyeshach.plugin).forEach {
            if (Trait::class.java.isAssignableFrom(it) && Trait::class.java != it) {
                try {
                    val trait = it.newInstance() as Trait
                    if (trait is Listener) {
                        Bukkit.getPluginManager().registerEvents(trait, Adyeshach.plugin)
                    }
                    traits.add(trait)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
    }
}