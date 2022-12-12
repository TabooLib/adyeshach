package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.common.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultItem
 *
 * @author 坏黑
 * @since 2022/6/29 19:05
 */
abstract class DefaultItem(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyItem {

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            spawn(viewer) {
                // 创建客户端对应表
                registerClientEntity(viewer)
                // 修正掉落物信息
                setMetadata("item", getItem())
                // 生成实体
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntity(viewer, entityType, index, normalizeUniqueId, position.toLocation())
                // 修正向量
                submit(delay = 1) {
                    setNoGravity(true)
                    setVelocity(Vector(0, 0, 0))
                }
            }
        } else {
            super.visible(viewer, false)
        }
    }
    override fun setItem(itemStack: ItemStack) {
        setMetadata("item", itemStack)
        respawn()
    }

    override fun getItem(): ItemStack {
        return getMetadata("item")
    }
}