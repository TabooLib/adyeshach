package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.ClientEntity
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyEntity
import ink.ptms.adyeshach.impl.DefaultAdyeshachEntityFinder.Companion.clientEntityMap
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultEntity
 *
 * @author 坏黑
 * @since 2022/6/20 00:34
 */
abstract class DefaultEntity(entityType: EntityTypes) : DefaultEntityInstance(entityType), AdyEntity {

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            spawn(viewer) {
                val clientId = normalizeUniqueId
                // 创建客户端对应表
                clientEntityMap.computeIfAbsent(viewer.name) { ConcurrentHashMap() }[index] = ClientEntity(this, clientId)
                // 生成实体
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntity(viewer, entityType, index, clientId, position.toLocation())
            }
        } else {
            destroy(viewer) {
                // 销毁实体
                Adyeshach.api().getMinecraftAPI().getEntityOperator().destroyEntity(viewer, index)
                // 移除客户端对应表
                clientEntityMap[viewer.name]?.remove(index)
            }
        }
    }
}