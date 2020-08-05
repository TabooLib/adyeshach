package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.java.JavaUtil
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.DyeColor
import org.bukkit.entity.Cat
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyCat(owner: Player) : AdyEntityTameable(owner, EntityTypes.CAT) {

    init {
        registerMeta(18, "type", Cat.Type.TABBY.ordinal)
        registerMeta(21, "color", DyeColor.RED.ordinal)
    }

    fun setType(value: Cat.Type) {
        setMetadata("type", value.ordinal)
    }

    fun getType(): Cat.Type {
        return JavaUtil.valuesCatType().first { it.ordinal == getMetadata("type") }
    }

    fun setCollarColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    fun getCollarColor(): DyeColor {
        return JavaUtil.valuesDyeColor().first { it.ordinal == getMetadata("color") }
    }
}