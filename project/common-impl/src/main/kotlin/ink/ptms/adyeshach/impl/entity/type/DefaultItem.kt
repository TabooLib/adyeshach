package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyItem
import ink.ptms.adyeshach.core.util.toItem
import ink.ptms.adyeshach.impl.util.ifTrue
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultItem
 *
 * @author 坏黑
 * @since 2022/6/29 19:05
 */
abstract class DefaultItem(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyItem {

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        // 伴生实体禁止外部直接操作可见性
        if (isCompanion()) return false
        return handleVisibleInternal(viewer, visible)
    }

    override fun handleVisibleInternal(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            prepareSpawn(viewer) {
                // 修正掉落物信息
                setMetadata("item", getItem())
                // 生成实体
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntity(viewer, entityType, index, normalizeUniqueId, clientPosition.toLocation())
                // 修正向量
                submit(delay = 1) {
                    setNoGravity(true)
                    sendVelocity(Vector(0, 0, 0))
                }
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

    override fun setItem(itemStack: ItemStack) {
        setMetadata("item", itemStack)
        respawn()
    }

    override fun getItem(): ItemStack {
        return getMetadata("item")
    }

    override fun setCustomMeta(key: String, value: String?): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "item" -> {
                setItem(value?.toItem() ?: ItemStack(Material.APPLE))
                true
            }
            else -> false
        }
    }
}