package ink.ptms.adyeshach.core.entity

import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.Rideable
 *
 * @author 坏黑
 * @since 2022/6/16 00:11
 */
interface Rideable {

    /**
     * 是否为载具（指存在任何骑乘单位）
     */
    fun isVehicle(): Boolean

    /**
     * 是否存在载具（指是否骑乘其他单位）
     */
    fun hasVehicle(): Boolean

    /**
     * 获取载具
     */
    fun getVehicle(): EntityInstance?

    /**
     * 是否有乘客
     */
    fun hasPassengers(): Boolean

    /**
     * 获取骑乘者
     */
    fun getPassengers(): List<EntityInstance>

    /**
     * 添加骑乘者
     */
    fun addPassenger(vararg entity: EntityInstance)

    /**
     * 移除骑乘者
     */
    fun removePassenger(vararg entity: EntityInstance)

    /**
     * 移除骑乘者
     */
    fun removePassenger(vararg id: String)

    /**
     * 清空骑乘者
     */
    fun clearPassengers()

    /**
     * 刷新骑乘者
     */
    fun refreshPassenger(viewer: Player)

    /**
     * 刷新骑乘者
     */
    fun refreshPassenger()

    /**
     * 验证骑乘者
     * 将会移除所有不在相同管理器的骑乘者 UUID
     */
    fun verifyPassenger()
}