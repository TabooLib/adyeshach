package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.ClientEntity
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyEntityLiving
import ink.ptms.adyeshach.impl.DefaultAdyeshachEntityFinder.Companion.clientEntityMap
import ink.ptms.adyeshach.impl.entity.DefaultEquipable
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultEntityLiving
 *
 * @author 坏黑
 * @since 2022/6/20 00:44
 */
abstract class DefaultEntityLiving(entityType: EntityTypes) : DefaultEntity(entityType), DefaultEquipable, AdyEntityLiving {

    @Expose
    val equipment = ConcurrentHashMap<EquipmentSlot, ItemStack>()

    @Expose
    var isDie = false

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            spawn(viewer) {
                val clientId = normalizeUniqueId
                // 创建客户端对应表
                clientEntityMap.computeIfAbsent(viewer.name) { ConcurrentHashMap() }[index] = ClientEntity(this, clientId)
                // 生成实体
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityLiving(viewer, entityType, index, clientId, position.toLocation())
                // 更新装备
                submit(delay = 1) { updateEquipment() }
                // 更新死亡状态
                submit(delay = 5) {
                    if (isDie) {
                        die(viewer = viewer)
                    }
                }
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

    override fun die(die: Boolean) {
        TODO("Not yet implemented")
    }

    override fun die(viewer: Player, die: Boolean) {
        TODO("Not yet implemented")
    }
}