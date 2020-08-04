package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.nms.NMS
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
        properties = CatProperties()
    }

    fun setType(value: Cat.Type) {
        getProperties().type = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityInt(18, value.ordinal))
    }

    fun getType(): Cat.Type {
        return getProperties().type
    }

    fun setCollarColor(value: DyeColor) {
        getProperties().color = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityInt(21, value.ordinal))
    }

    fun getCollarColor(): DyeColor {
        return getProperties().color
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    *super.metadata().toTypedArray(),
                    NMS.INSTANCE.getMetaEntityInt(18, type.ordinal),
                    NMS.INSTANCE.getMetaEntityInt(21, color.ordinal)
            )
        }
    }

    private fun getProperties(): CatProperties = properties as CatProperties

    private class CatProperties : EntityProperties.Tameable() {

        @Expose
        var type = Cat.Type.TABBY
        @Expose
        var color = DyeColor.RED
    }
}