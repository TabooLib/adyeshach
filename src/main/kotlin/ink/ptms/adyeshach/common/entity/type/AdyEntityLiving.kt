package ink.ptms.adyeshach.common.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.ClientEntity
import ink.ptms.adyeshach.common.entity.EntityEquipable
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.NumberConversions
import taboolib.common.platform.function.submit
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
open class AdyEntityLiving(entityTypes: EntityTypes) : AdyEntity(entityTypes), EntityEquipable {

    @Expose
    internal val equipment = ConcurrentHashMap<EquipmentSlot, ItemStack>()

    @Expose
    internal var isDie = false

    var isHandActive: Boolean
        get() = getMetadata("isHandActive")
        set(value) {
            setMetadata("isHandActive", value)
        }

    var activeHand: Boolean
        get() = getMetadata("activeHand")
        set(value) {
            setMetadata("activeHand", value)
        }

    var isInRiptideSpinAttack: Boolean
        get() = getMetadata("isInRiptideSpinAttack")
        set(value) {
            setMetadata("isInRiptideSpinAttack", value)
        }

    var arrowsInEntity: Int
        get() = getMetadata("arrowsInEntity")
        set(value) {
            setMetadata("arrowsInEntity", value)
        }

    var beeStingersInEntity: Int
        get() = getMetadata("beeStingersInEntity")
        set(value) {
            setMetadata("beeStingersInEntity", value)
        }

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            spawn(viewer) {
                val clientId = normalizeUniqueId
                // 创建客户端对应表
                AdyeshachAPI.clientEntityMap.computeIfAbsent(viewer.name) { ConcurrentHashMap() }[index] = ClientEntity(this, clientId)
                // 生成实体
                NMS.INSTANCE.spawnEntityLiving(viewer, entityType, index, clientId, position.toLocation())
                // 更新装备
                submit(delay = 1) {
                    updateEquipment()
                }
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
                NMS.INSTANCE.destroyEntity(viewer, index)
                // 移除客户端对应表
                AdyeshachAPI.clientEntityMap[viewer.name]?.remove(index)
            }
        }
    }

    override fun getEquipment(): Map<EquipmentSlot, ItemStack> {
        return HashMap(equipment)
    }

    override fun setItemInMainHand(itemStack: ItemStack) {
        equipment[EquipmentSlot.HAND] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.HAND, itemStack)
        }
    }

    override fun getItemInMainHand(): ItemStack? {
        return equipment[EquipmentSlot.HAND]
    }

    override fun setItemInOffHand(itemStack: ItemStack) {
        equipment[EquipmentSlot.OFF_HAND] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.OFF_HAND, itemStack)
        }
    }

    override fun getItemInOffHand(): ItemStack? {
        return equipment[EquipmentSlot.OFF_HAND]
    }

    override fun setHelmet(itemStack: ItemStack) {
        equipment[EquipmentSlot.HEAD] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.HEAD, itemStack)
        }
    }

    override fun getHelmet(): ItemStack? {
        return equipment[EquipmentSlot.HEAD]
    }

    override fun setChestplate(itemStack: ItemStack) {
        equipment[EquipmentSlot.CHEST] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.CHEST, itemStack)
        }
    }

    override fun getChestplate(): ItemStack? {
        return equipment[EquipmentSlot.CHEST]
    }

    override fun setLeggings(itemStack: ItemStack) {
        equipment[EquipmentSlot.LEGS] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.LEGS, itemStack)
        }
    }

    override fun getLeggings(): ItemStack? {
        return equipment[EquipmentSlot.LEGS]
    }

    override fun setBoots(itemStack: ItemStack) {
        equipment[EquipmentSlot.FEET] = itemStack
        forViewers {
            NMS.INSTANCE.updateEquipment(it, index, EquipmentSlot.FEET, itemStack)
        }
    }

    override fun getBoots(): ItemStack? {
        return equipment[EquipmentSlot.FEET]
    }

    override fun clear() {
        equipment.clear()
    }

    fun setEquipment(equipmentSlot: EquipmentSlot, itemStack: ItemStack) {
        when (equipmentSlot) {
            EquipmentSlot.HAND -> setItemInMainHand(itemStack)
            EquipmentSlot.OFF_HAND -> setItemInOffHand(itemStack)
            EquipmentSlot.FEET -> setBoots(itemStack)
            EquipmentSlot.LEGS -> setLeggings(itemStack)
            EquipmentSlot.CHEST -> setChestplate(itemStack)
            EquipmentSlot.HEAD -> setHelmet(itemStack)
        }
    }

    fun getEquipment(equipmentSlot: EquipmentSlot): ItemStack? {
        return when (equipmentSlot) {
            EquipmentSlot.HAND -> getItemInMainHand()
            EquipmentSlot.OFF_HAND -> getItemInOffHand()
            EquipmentSlot.FEET -> getBoots()
            EquipmentSlot.LEGS -> getLeggings()
            EquipmentSlot.CHEST -> getChestplate()
            EquipmentSlot.HEAD -> getHelmet()
        }
    }

    fun updateEquipment() {
        forViewers {
            EquipmentSlot.values().forEach { e -> NMS.INSTANCE.updateEquipment(it, index, e, getEquipment(e) ?: return@forEach) }
        }
    }

    /**
     * 尸体状态
     */
    fun die(die: Boolean = true) {
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

    /**
     * 对指定玩家播放尸体状态
     */
    fun die(viewer: Player, die: Boolean = true) {
        if (die) {
            val healthMeta = getAvailableEntityMeta().firstOrNull { it.key == "health" }!!
            NMS.INSTANCE.updateEntityMetadata(viewer, index, NMS.INSTANCE.getMetaEntityFloat(healthMeta.index, NumberConversions.toFloat(-1)))
            submit(delay = 15) {
                NMS.INSTANCE.updateEntityMetadata(viewer, index, NMS.INSTANCE.getMetaEntityFloat(healthMeta.index, NumberConversions.toFloat(1)))
            }
        } else {
            visible(viewer, true)
        }
    }

    fun setHealth(value: Float) {
        setMetadata("health", value)
    }

    fun getHealth(): Float {
        return getMetadata("health")
    }

    fun setPotionEffectColor(value: Color) {
        setMetadata("potionEffectColor", value.asRGB())
    }

    fun getPotionEffectColor(): Color {
        return Color.fromRGB(getMetadata<Int>("potionEffectColor").let { if (it == -1) 0 else it })
    }
}