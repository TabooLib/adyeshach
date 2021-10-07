package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Parrot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyParrot : AdyEntityTameable(EntityTypes.PARROT) {

    fun setColor(color: Parrot.Variant) {
        setMetadata("color", color.ordinal)
    }

    fun getColor(): Parrot.Variant {
        return Parrot.Variant.values()[getMetadata("color")]
    }
}
