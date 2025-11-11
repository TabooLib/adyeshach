package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import ink.ptms.adyeshach.core.util.toItem
import ink.ptms.adyeshach.impl.entity.DefaultEquipable
import ink.ptms.adyeshach.impl.util.ifTrue
import ink.ptms.adyeshach.impl.util.toRGB
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.common5.cbool
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
    override var isDie = false

    override var isEquipmentRefreshOnSpawn = false

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            prepareSpawn(viewer) {
                viewPlayers.visible += viewer.name
                // 创建客户端对应表
                registerClientEntity(viewer)
                // 添加到可见实体索引
                updateVisibleEntityIndex(viewer, true)
                // 生成实体
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityLiving(viewer, entityType, index, normalizeUniqueId, position.toLocation())
                // 更新装备
                if (isEquipmentRefreshOnSpawn) {
                    submit(delay = 1) { updateEquipment() }
                }
                // 更新死亡状态
                if (isDie) {
                    submit(delay = 5) { die(viewer = viewer) }
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

    override fun die(die: Boolean) {
        isDie = die
        if (isDie) {
            setHealth(-1f)
            submit(delay = 15) {
                if (isDie) {
                    setHealth(1f)
                }
            }
        } else {
            respawn()
        }
    }

    @Deprecated("不安全的实现，请使用 die(Boolean)")
    override fun die(viewer: Player, die: Boolean) {
        if (die) {
            val healthMeta = getAvailableEntityMeta().firstOrNull { it.key == "health" }!!
            val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
            val metadataHandler = Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler()
            operator.updateEntityMetadata(viewer, index, listOf(metadataHandler.createFloatMeta(healthMeta.index, -1f)))
            submit(delay = 15) {
                operator.updateEntityMetadata(viewer, index, listOf(metadataHandler.createFloatMeta(healthMeta.index, -1f)))
            }
        } else {
            visible(viewer, true)
        }
    }

    @Suppress("SpellCheckingInspection")
    override fun setCustomMeta(key: String, value: String?): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "die" -> {
                die(value?.cbool ?: false)
                true
            }

            "helmet", "head" -> {
                setHelmet(value?.toItem() ?: ItemStack(Material.AIR))
                true
            }

            "chestplate", "chest" -> {
                setChestplate(value?.toItem() ?: ItemStack(Material.AIR))
                true
            }

            "leggings", "legs" -> {
                setLeggings(value?.toItem() ?: ItemStack(Material.AIR))
                true
            }

            "boots", "feet" -> {
                setBoots(value?.toItem() ?: ItemStack(Material.AIR))
                true
            }

            "hand", "mainhand", "main_hand" -> {
                setItemInMainHand(value?.toItem() ?: ItemStack(Material.AIR))
                true
            }

            "offhand", "off_hand" -> {
                setItemInOffHand(value?.toItem() ?: ItemStack(Material.AIR))
                true
            }

            "potioneffectcolor", "potion_effect_color" -> {
                // 对 RGB 写法进行兼容
                if (value != null && value.contains(',')) {
                    setPotionEffectColor(value.toRGB())
                    true
                } else {
                    false
                }
            }

            else -> false
        }
    }
}