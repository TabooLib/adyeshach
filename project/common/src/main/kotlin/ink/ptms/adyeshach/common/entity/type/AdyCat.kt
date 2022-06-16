package ink.ptms.adyeshach.common.entity.type


import org.bukkit.DyeColor
import org.bukkit.entity.Cat

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
abstract class AdyCat : AdyEntityTameable() {

    var isLying: Boolean
        get() = getMetadata("isLying")
        set(value) {
            setMetadata("isLying", value)
        }

    var isRelaxed: Boolean
        get() = getMetadata("isRelaxed")
        set(value) {
            setMetadata("isRelaxed", value)
        }

    open fun setType(value: Cat.Type) {
        setMetadata("type", value.ordinal)
    }

    open fun getType(): Cat.Type {
        return Cat.Type.values()[getMetadata("type")]
    }

    open fun setCollarColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    open fun getCollarColor(): DyeColor {
        return DyeColor.values()[getMetadata("color")]
    }
}