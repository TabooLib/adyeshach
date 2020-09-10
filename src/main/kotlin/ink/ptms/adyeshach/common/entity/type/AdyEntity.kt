package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityFireball
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityThrowable
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
                    // 火焰弹单位初始化，取消客户端向量计算
                    if (this is EntityFireball) {
                        it.yaw = 0f
                        it.pitch = 0f
                    }
                    it
                })
            }
            // 投掷物单位初始化，取消客户端重力计算
            if (this is EntityThrowable) {
                setNoGravity(true)
            }
        } else {
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
            }
        }
    }
}