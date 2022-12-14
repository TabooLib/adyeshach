package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.entity.EntityEquipable
import org.bukkit.Color
import org.bukkit.entity.Player

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
interface AdyEntityLiving : AdyEntity, EntityEquipable {

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

    /**
     * 实体是否处于激流
     */
    var isInRiptideSpinAttack: Boolean
        get() = getMetadata("isInRiptideSpinAttack")
        set(value) {
            setMetadata("isInRiptideSpinAttack", value)
        }

    /**
     * 实体身上存在的箭矢，经过测试这个属性只对玩家类型有效
     */
    var arrowsInEntity: Int
        get() = getMetadata("arrowsInEntity")
        set(value) {
            setMetadata("arrowsInEntity", value)
        }

    /**
     * 实体身上存在的蜂刺，经过测试这个属性只对玩家类型有效
     */
    var beeStingersInEntity: Int
        get() = getMetadata("beeStingersInEntity")
        set(value) {
            setMetadata("beeStingersInEntity", value)
        }

    /**
     * 切换数据包实体的死亡状态，该功能是通过利用客户端渲染漏洞实现，无法保证稳定性
     */
    fun die(die: Boolean = true)

    /**
     * 对给定玩家切换数据包实体的死亡状态
     */
    @Deprecated("不安全的实现，请使用 die(Boolean)")
    fun die(viewer: Player, die: Boolean = true)

    /**
     * 设置实体生命
     */
    fun setHealth(value: Float) {
        setMetadata("health", value)
    }

    /**
     * 获取实体生命
     */
    fun getHealth(): Float {
        return getMetadata("health")
    }

    /**
     * 设置实体药水颜色
     */
    fun setPotionEffectColor(value: Color) {
        setMetadata("potionEffectColor", value.asRGB())
    }

    /**
     * 获取实体药水颜色
     */
    fun getPotionEffectColor(): Color {
        return Color.fromRGB(getMetadata<Int>("potionEffectColor").coerceAtLeast(0))
    }
}