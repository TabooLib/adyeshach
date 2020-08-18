package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.java.JavaUtil
import org.bukkit.DyeColor
import org.bukkit.entity.Cat

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyCat() : AdyEntityTameable(EntityTypes.CAT) {

    /**
     * 仅 1.16, 1.15 有属性
     */
    init {
        registerMeta(at(11500 to 18), "type", Cat.Type.TABBY.ordinal)
        registerMeta(at(11500 to 21), "color", DyeColor.RED.ordinal)
    }

    fun setType(value: Cat.Type) {
        setMetadata("type", value.ordinal)
    }

    fun getType(): Cat.Type {
        return JavaUtil.valuesCatType()[getMetadata("type")]
    }

    fun setCollarColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    fun getCollarColor(): DyeColor {
        return JavaUtil.valuesDyeColor()[getMetadata("color")]
    }
}