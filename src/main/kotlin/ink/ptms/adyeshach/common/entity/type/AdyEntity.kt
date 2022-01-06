package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.*
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
open class AdyEntity(entityTypes: EntityTypes) : EntityInstance(entityTypes) {

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            spawn(viewer) {
                val clientId = normalizeUniqueId
                // 创建客户端对应表
                AdyeshachAPI.clientEntityMap.computeIfAbsent(viewer.name) { ConcurrentHashMap() }[index] = ClientEntity(this, clientId)
                // 生成实体
                NMS.INSTANCE.spawnEntity(viewer, entityType, index, clientId, position.toLocation())
            }
        } else {
            destroy(viewer) {
                // 销毁实体
                NMS.INSTANCE.destroyEntity(viewer, index)
                // 移除客户端对应表
                AdyeshachAPI.clientEntityMap[viewer.name]?.remove(index)
            }
        }
    }
}