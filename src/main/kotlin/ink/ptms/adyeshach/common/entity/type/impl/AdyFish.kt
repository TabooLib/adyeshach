package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.api.nms.NMS
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyFish(owner: Player, entityTypes: EntityTypes) : AdyEntityLiving(owner, entityTypes), MetadataExtend {

    fun setFromBucket(value: Boolean) {
        getProperties().fromBucket = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityBoolean(15, value))
    }

    fun isFromBucket(): Boolean {
        return getProperties().fromBucket
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(NMS.INSTANCE.getMetaEntityBoolean(15, fromBucket))
        }
    }

    private fun getProperties(): EntityProperties.Fish = properties as EntityProperties.Fish

}