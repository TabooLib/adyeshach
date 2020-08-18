package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Parrot
import org.bukkit.entity.Parrot.Variant.*

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyParrot() : AdyEntityLiving(EntityTypes.PARROT) {

    init {
        registerMeta(at(11500 to 18, 11400 to 17, 11200 to 15), "color", 0)
    }

    fun setColor(color: Parrot.Variant) {
        setMetadata(
            "color",
            when (color) {
                RED -> 0
                BLUE -> 1
                GREEN -> 2
                CYAN -> 3
                GRAY -> 4
            }
        )
    }

    fun getColor(): Parrot.Variant {
        return values()[getMetadata("color")]
    }

}