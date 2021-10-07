package ink.ptms.adyeshach.common.entity.type


import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.DyeColor
import org.bukkit.entity.Cat

/**
 * 仅 1.16, 1.15 有属性
 *
 * @author sky
 * @since 2020-08-04 19:30
 */
class AdyCat : AdyEntityTameable(EntityTypes.CAT) {

    fun setType(value: Cat.Type) {
        setMetadata("type", value.ordinal)
    }

    fun getType(): Cat.Type {
        return Cat.Type.values()[getMetadata("type")]
    }

    fun setCollarColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    fun getCollarColor(): DyeColor {
        return DyeColor.values()[getMetadata("color")]
    }

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
}