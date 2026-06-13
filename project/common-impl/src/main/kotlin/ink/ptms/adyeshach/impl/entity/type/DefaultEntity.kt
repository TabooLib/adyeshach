package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyEntity
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultEntity
 *
 * @author 坏黑
 * @since 2022/6/20 00:34
 */
abstract class DefaultEntity(entityType: EntityTypes) : DefaultEntityInstance(entityType), AdyEntity {

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        // 伴生实体禁止外部直接操作可见性
        if (isCompanion()) return false
        return handleVisibleInternal(viewer, visible)
    }

    /**
     * 内部可见性处理（用于伴生实体同步）
     */
    protected open fun handleVisibleInternal(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            prepareSpawn(viewer) {
                // 生成实体
                val bodyLoc = positionHandler.spawnBodyLocation()
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntity(
                    player = viewer,
                    entityType = entityType,
                    entityId = index,
                    uuid = normalizeUniqueId,
                    location = bodyLoc,
                    headYaw = positionHandler.headYaw(),
                )
            }
        } else {
            prepareDestroy(viewer) {
                // 销毁实体
                Adyeshach.api().getMinecraftAPI().getEntityOperator().destroyEntity(viewer, index)
            }
        }
    }

    override fun handleCompanionVisible(viewer: Player, visible: Boolean) {
        handleVisibleInternal(viewer, visible)
    }
}
