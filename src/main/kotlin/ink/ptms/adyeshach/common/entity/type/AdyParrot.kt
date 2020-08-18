package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.java.JavaUtil
import org.bukkit.entity.Parrot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyParrot() : AdyEntityTameable(EntityTypes.PARROT) {

    init {
        registerMeta(at(11500 to 18, 11400 to 17, 11200 to 15), "color", Parrot.Variant.RED.ordinal)
    }

    fun setColor(color: Parrot.Variant) {
        setMetadata("color", color.ordinal)
    }

    fun getColor(): Parrot.Variant {
        return JavaUtil.valueParrotVariant()[getMetadata("color")]
    }
}
