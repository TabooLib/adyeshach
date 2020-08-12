package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
open class AdyEntity(entityTypes: EntityTypes) : EntityInstance(entityTypes) {

    override fun spawn(location: Location) {
        super.spawn(location)
        NMS.INSTANCE.spawnEntity(owner!!, entityType.getEntityTypeNMS(), index, UUID.randomUUID(), location)
    }
}