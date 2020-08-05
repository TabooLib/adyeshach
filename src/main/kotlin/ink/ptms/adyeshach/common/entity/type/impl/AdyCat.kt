package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
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
        registerMeta(18, "type", 0)
        registerMeta(21, "color", 14)
    }

    fun setType(value: Cat.Type) {
        setMetadata("type", value.ordinal)
    }

    fun getType(): Cat.Type {
        return getMetadata("type")
    }

    fun setCollarColor(value: DyeColor) {
        getProperties().color = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityInt(21, value.ordinal))
    }

    fun getCollarColor(): DyeColor {
        return getProperties().color
    }
}