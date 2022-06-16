package ink.ptms.adyeshach.common.entity.type

import org.bukkit.entity.Parrot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyParrot : AdyEntityTameable() {

    open fun setColor(color: Parrot.Variant) {
        setMetadata("color", color.ordinal)
    }

    open fun getColor(): Parrot.Variant {
        return Parrot.Variant.values()[getMetadata("color")]
    }
}
