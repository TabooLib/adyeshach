package ink.ptms.adyeshach.core.entity

import ink.ptms.adyeshach.core.bukkit.BukkitPose
import org.bukkit.ChatColor

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.GenericEntity
 *
 * @author 坏黑
 * @since 2022/6/16 00:09
 */
interface GenericEntity {

    /**
     * 是否可见名称
     */
    var isNameTagVisible: Boolean

    /**
     * 是否碰撞
     */
    var isCollision: Boolean

    /**
     * 发光颜色
     */
    var glowingColor: ChatColor

    /**
     * 实体在细雪中的停留的时间（应该是）
     */
    var ticksFrozenInPowderedSnow: Int

    /**
     * 获取单位展示名称（没有 customName 则获取 EntityType 名称）
     */
    fun getDisplayName(): String

    /**
     * 是否着火
     */
    fun isFired(): Boolean

    /**
     * 是否潜行
     */
    fun isSneaking(): Boolean

    /**
     * 是否疾跑
     */
    fun isSprinting(): Boolean

    /**
     * 是否游泳
     */
    fun isSwimming(): Boolean

    /**
     * 是否不可见
     */
    fun isInvisible(): Boolean

    /**
     * 是否发光
     */
    fun isGlowing(): Boolean

    /**
     * 是否使用鞘翅飞行
     */
    fun isFlyingElytra(): Boolean

    /**
     * 是否无重力（仅在弓箭、掉落物等客户端计算重力的实体有效）
     */
    fun isNoGravity(): Boolean

    /**
     * 设置着火
     */
    fun setFired(onFire: Boolean)

    /**
     * 设置潜行
     */
    fun setSneaking(sneaking: Boolean)

    /**
     * 设置疾跑
     */
    fun setSprinting(sprinting: Boolean)

    /**
     * 设置游泳
     */
    fun setSwimming(swimming: Boolean)

    /**
     * 设置为不可见
     */
    fun setInvisible(invisible: Boolean)

    /**
     * 设置为发光
     */
    fun setGlowing(glowing: Boolean)

    /**
     * 设置为鞘翅飞行
     */
    fun setFlyingElytra(flyingElytra: Boolean)

    /**
     * 设置为无重力
     */
    fun setNoGravity(noGravity: Boolean)

    /**
     * 设置自定义名称是否可见
     */
    fun setCustomNameVisible(value: Boolean)

    /**
     * 自定义名称是否可见
     */
    fun isCustomNameVisible(): Boolean

    /**
     * 设置自定义名称
     */
    fun setCustomName(value: String)

    /**
     * 获取自定义名称
     */
    fun getCustomName(): String

    /**
     * 获取自定义名称（原始信息）
     */
    fun getCustomNameRaw(): String

    /**
     * 设置实体形态
     */
    fun setPose(pose: BukkitPose)

    /**
     * 获取实体形态
     */
    fun getPose(): BukkitPose
}