package ink.ptms.adyeshach.core.entity.type

import org.bukkit.entity.Parrot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyParrot : AdyEntityTameable {

    fun setColor(color: Parrot.Variant) {
        setMetadata("color", color.ordinal)
    }

    fun getColor(): Parrot.Variant {
        return Parrot.Variant.values()[getMetadata("color")]
    }
}
