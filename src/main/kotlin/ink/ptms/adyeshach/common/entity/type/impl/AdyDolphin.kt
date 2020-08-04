package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitCreeperState
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyDolphin(owner: Player) : AdyEntityLiving(owner, EntityTypes.DOLPHIN), MetadataExtend {

    init {
        properties = DolphinProperties()
    }

    fun setFindTreasure(value: Boolean) {
        getProperties().findTreasure = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityBoolean(16, value))
    }

    fun isFindTreasure(): Boolean {
        return getProperties().findTreasure
    }

    fun setHasFish(value: Boolean) {
        getProperties().hasFish = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityBoolean(17, value))
    }

    fun isHasFish(): Boolean {
        return getProperties().hasFish
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    NMS.INSTANCE.getMetaEntityBoolean(16, findTreasure),
                    NMS.INSTANCE.getMetaEntityBoolean(17, hasFish)
            )
        }
    }

    private fun getProperties(): DolphinProperties = properties as DolphinProperties

    private class DolphinProperties : EntityProperties() {

        @Expose
        var findTreasure = false
        @Expose
        var hasFish = false
    }
}