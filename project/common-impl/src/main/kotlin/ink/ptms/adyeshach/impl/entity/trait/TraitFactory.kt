package ink.ptms.adyeshach.impl.entity.trait

import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake

object TraitFactory {

    val traits = ArrayList<Trait>()

    @Awake(LifeCycle.ENABLE)
    fun init() {
        runningClasses.forEach {
            if (Trait::class.java.isAssignableFrom(it) && Trait::class.java != it) {
                val trait = it.getInstance()?.get() as? Trait
                if (trait != null) {
                    traits.add(trait)
                }
            }
        }
    }

    fun getTraitById(id: String): Trait? {
        return traits.firstOrNull { it.id() == id }
    }
}