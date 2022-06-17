package ink.ptms.adyeshach.common.api

import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.MinecraftEntityOperator
 *
 * @author 坏黑
 * @since 2022/6/15 18:09
 */
interface MinecraftEntityOperator {

    /**
     * 移除数据包实体
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     */
    fun destroyEntity(player: Player, entityId: Int)

    /**
     * 传送数据包实体到另一个位置
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param location 传送位置
     */
    fun teleportEntity(player: Player, entityId: Int, location: Location)

    /**
     * 向客户端发送 PacketPlayOutEntity.PacketPlayOutRelEntityMove 数据包，该方法未被 Adyeshach 利用
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param x x 轴移动量
     * @param y y 轴移动量
     * @param z z 轴移动量
     */
    fun relMoveEntity(player: Player, entityId: Int, x: Double, y: Double, z: Double)

    /**
     * 更新数据包实体的移动量，在 Adyeshach 中仅在 Item 类型的实体生成中被使用，用于防止实体飞溅
     *
     * Adyeshach 在控制实体移动时未使用该数据包
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param velocity 移动量
     */
    fun updateEntityVelocity(player: Player, entityId: Int, vector: Vector)

    /**
     * 更新数据包实体的头部方向
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param yaw yaw 方向
     * @param pitch pitch 方向
     */
    fun updateHeadRotation(player: Player, entityId: Int, yaw: Float, pitch: Float)

    /**
     * 更新数据包实体的装备信息
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param slot 装备槽
     * @param itemStack 物品对象
     */
    fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack)

    /**
     * 更新数据包实体的骑乘信息
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param passengers 所有乘坐者的实体序号
     */
    fun updatePassengers(player: Player, entityId: Int, vararg passengers: Int)

    /**
     * 更新数据包实体的元数据信息，元数据包从 [MinecraftEntityMetadataHandler] 接口中构建
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param metadata 元数据包信息
     */
    fun updateEntityMetadata(player: Player, entityId: Int, vararg metadata: MinecraftMeta)

    /**
     * 更新数据包实体对动作
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param animation 动作
     */
    fun updateEntityAnimation(player: Player, entityId: Int, animation: BukkitAnimation)

    /**
     * 更新数据包实体的附着状态，可能是应用于一些带有可视目标锁定的实体，该方法未被 Adyeshach 利用
     *
     * @param player 数据包接收人
     * @param attached 原实体
     * @param holding 目标实体
     */
    fun updateEntityAttach(player: Player, attached: Int, holding: Int)

    /**
     * 使玩家类型数据包实体的进入睡眠状态，该方法用于 1.13 及以下版本。
     *
     * 在 1.13 以上版本调用时会产生异常。
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param location 床的坐标
     */
    fun updatePlayerSleeping(player: Player, entityId: Int, location: Location)
}