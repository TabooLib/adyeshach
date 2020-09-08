package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityFireball
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Player
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 18:28
 */
open class AdyEntity(entityTypes: EntityTypes) : EntityInstance(entityTypes) {

    override fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            spawn(viewer) {
                NMS.INSTANCE.spawnEntity(viewer, entityType, index, UUID.randomUUID(), position.toLocation().let {
                    if (this is EntityFireball) {
                        it.yaw = 0f
                        it.pitch = 0f
                    }
                    it
                })
            }
        } else {
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
            }
        }
    }
}