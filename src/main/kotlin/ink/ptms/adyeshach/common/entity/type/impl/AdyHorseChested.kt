package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.api.nms.NMS
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyHorseChested(owner: Player, entityTypes: EntityTypes) : AdyHorseBase(owner, entityTypes) {

    fun setHasChest(value: Boolean) {
        getProperties().chest = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityBoolean(18, value))
    }

    fun isHasChest(): Boolean {
        return getProperties().chest
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    *super.metadata().toTypedArray(),
                    NMS.INSTANCE.getMetaEntityBoolean(18, chest)
            )
        }
    }

    private fun getProperties(): EntityProperties.HorseChested = properties as EntityProperties.HorseChested
}