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
open class AdyZombie(owner: Player, entityTypes: EntityTypes) : AdyEntityAgeable(owner, entityTypes) {

    fun setDrowning(value: Boolean) {
        getProperties().drowning = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityBoolean(15, value))
    }

    fun isDrowning(): Boolean {
        return getProperties().drowning
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    *super.metadata().toTypedArray(),
                    NMS.INSTANCE.getMetaEntityBoolean(17, drowning)
            )
        }
    }

    private fun getProperties(): EntityProperties.Zombie = properties as EntityProperties.Zombie

}