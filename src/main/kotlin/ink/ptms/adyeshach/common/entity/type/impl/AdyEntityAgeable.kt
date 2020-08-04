package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.nms.NMS
import net.minecraft.server.v1_16_R1.EntityLiving
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
abstract class AdyEntityAgeable(owner: Player, entityTypes: EntityTypes) : AdyEntityLiving(owner, entityTypes), MetadataExtend {

    fun setBaby(value: Boolean) {
        getProperties().baby = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityBoolean(15, value))
    }

    fun isBaby(): Boolean {
        return getProperties().baby
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(NMS.INSTANCE.getMetaEntityBoolean(15, baby))
        }
    }

    private fun getProperties(): EntityProperties.Ageable = properties as EntityProperties.Ageable

}