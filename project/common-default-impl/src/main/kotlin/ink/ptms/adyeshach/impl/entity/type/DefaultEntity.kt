package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.ClientEntity
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyEntity
import ink.ptms.adyeshach.impl.DefaultAdyeshachEntityFinder.Companion.clientEntityMap
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
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
                // 创建客户端对应表
                registerClientEntity(viewer)
                // 生成实体
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntity(viewer, entityType, index, normalizeUniqueId, position.toLocation())
                // 确保让一些特殊的实体看向正确的位置
                // 矿车，凋零头
                submit(delay = 5) {
                    setHeadRotation(yaw, pitch, forceUpdate = true)
                }
            }
        } else {
            destroy(viewer) {
                // 销毁实体
                Adyeshach.api().getMinecraftAPI().getEntityOperator().destroyEntity(viewer, index)
                // 移除客户端对应表
                unregisterClientEntity(viewer)
            }
        }
    }

    protected fun registerClientEntity(viewer: Player) {
        clientEntityMap.computeIfAbsent(viewer.name) { ConcurrentHashMap() }[index] = ClientEntity(this, normalizeUniqueId)
    }

    protected fun unregisterClientEntity(viewer: Player) {
        clientEntityMap[viewer.name]?.remove(index)
    }
}