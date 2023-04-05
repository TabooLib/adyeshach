package ink.ptms.adyeshach.core.entity.type

import org.bukkit.entity.Frog

/**
 * @author sky
 * @date 2020/8/4 22:18
 */
interface AdyFrog : AdyEntityAgeable {

    fun setFrogVariant(variant: Frog.Variant) {
        setMetadata("frogVariant", variant)
    }

    fun getFrogVariant(): Frog.Variant {
        return getMetadata("frogVariant")
    }

    fun setTongueTarget(target: Int) {
        setMetadata("tongueTarget", target)
    }

    fun getTongueTarget(): Int {
        return getMetadata("tongueTarget")
    }
}