package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.ClientEntity
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyEntity
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
            prepareSpawn(viewer) {
                viewPlayers.visible += viewer.name
                // 创建客户端对应表
                registerClientEntity(viewer)
                // 添加到可见实体索引
                updateVisibleEntityIndex(viewer, true)
                // 生成实体
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntity(viewer, entityType, index, normalizeUniqueId, position.toLocation())
                // 强制更新一次视角朝向，确保让一些特殊的实体看向正确的位置
                // 矿车，凋零头
                if (isRotationFixOnSpawn) {
                    submit(delay = 5) { setHeadRotation(yaw, pitch, forceUpdate = true) }
                }
            }
        } else {
            prepareDestroy(viewer) {
                viewPlayers.visible -= viewer.name
                // 从可见实体索引中移除
                updateVisibleEntityIndex(viewer, false)
                // 销毁实体
                Adyeshach.api().getMinecraftAPI().getEntityOperator().destroyEntity(viewer, index)
                // 移除客户端对应表
                unregisterClientEntity(viewer)
            }
        }
    }

    /**
     * 更新可见实体索引
     */
    protected fun updateVisibleEntityIndex(player: Player, visible: Boolean) {
        val finder = Adyeshach.api().getEntityFinder()
        if (visible) {
            finder.addVisibleEntity(player, this)
        } else {
            finder.removeVisibleEntity(player, this)
        }
    }

    protected fun registerClientEntity(viewer: Player) {
        if (useClientEntityMap) {
            val map = clientEntityMap.getOrCreate(viewer) { ConcurrentHashMap() } ?: return
            map[index] = ClientEntity(this)
        }
    }

    protected fun unregisterClientEntity(viewer: Player) {
        if (useClientEntityMap) {
            clientEntityMap[viewer]?.remove(index)
        }
    }
}