package ink.ptms.adyeshach.common.bukkit.nms

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

/**
 * 直接使用该接口中的方法不是数据包实体生成的全部流程，还需要在 [MinecraftEntityMetadataHandler] 中生成元数据
 *
 * @author 坏黑
 * @since 2022/6/15 18:06
 */
interface MinecraftEntitySpawner {

    /**
     * 生成数据包实体
     *
     * @param player 数据包接收人
     * @param entityType 实体类型
     * @param entityId 实体序号
     * @param uuid 实体 UUID
     * @param location 生成坐标
     */
    fun spawnEntity(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location)

    /**
     * 在 1.18 及以下版本生成 EntityLiving 类型的数据包实体，在 1.19 版本中被 [spawnEntity] 取代。
     *
     * 在 1.19 及以上版本调用时会产生异常。
     *
     * @param player 数据包接收人
     * @param entityType 实体类型
     * @param entityId 实体序号
     * @param uuid 实体 UUID
     * @param location 实体坐标
     */
    fun spawnEntityLiving(player: Player, entityType: EntityTypes, entityId: Int, uuid: UUID, location: Location)

    /**
     * 生成玩家类型的数据包实体
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param uuid 实体 UUID
     * @param location 实体坐标
     */
    fun spawnNamedEntity(player: Player, entityId: Int, uuid: UUID, location: Location)

    /**
     * 生成坠落的方块类型的数据包实体
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param uuid 实体 UUID
     * @param location 实体坐标
     * @param material 材质
     * @param data 附加值
     */
    fun spawnEntityFallingBlock(player: Player, entityId: Int, uuid: UUID, location: Location, material: Material, data: Byte)

    /**
     * 生成经验球类型的数据包实体
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param location 实体坐标
     * @param amount 数量
     */
    fun spawnEntityExperienceOrb(player: Player, entityId: Int, location: Location, amount: Int)

    /**
     * 在 1.18 及以下版本生成 EntityPainting 类型的数据包实体，在 1.19 版本中被 [spawnEntity] 取代
     *
     * 在 1.19 及以上版本调用时会产生异常。
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param location 实体坐标
     * @param direction 实体朝向
     * @param painting 画类型
     */
    fun spawnEntityPainting(player: Player, entityId: Int, uuid: UUID, location: Location, direction: BukkitDirection, painting: BukkitPaintings)
}