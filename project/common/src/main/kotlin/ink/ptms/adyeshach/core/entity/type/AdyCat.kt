package ink.ptms.adyeshach.core.entity.type


import org.bukkit.DyeColor
import org.bukkit.entity.Cat

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
interface AdyCat : AdyEntityTameable {

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

    fun setType(value: Cat.Type) {
        setMetadata("type", value.ordinal)
    }

    fun getType(): Cat.Type {
        return getMetadata("type")
    }

    fun setCollarColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    fun getCollarColor(): DyeColor {
        return DyeColor.values()[getMetadata("color")]
    }
}