package ink.ptms.adyeshach.core

import ink.ptms.adyeshach.core.bukkit.BukkitPaintings
import ink.ptms.adyeshach.core.bukkit.BukkitParticles
import ink.ptms.adyeshach.core.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.TropicalFish
import org.bukkit.material.MaterialData
import org.bukkit.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.MinecraftHelper
 *
 * @author 坏黑
 * @since 2022/6/15 18:10
 */
interface MinecraftHelper {

    /**
     * 将 [EntityTypes] 转换为 NMS 中的 EntityType 类型
     */
    fun adapt(type: EntityTypes): Any

    /**
     * 将 Location 转换为 NMS 中的 BlockPosition 类型
     */
    fun adapt(location: Location): Any

    /**
     * 将 [BukkitPaintings] 转换为 NMS 中的 Paintings 类型
     */
    fun adapt(paintings: BukkitPaintings): Any

    /**
     * 将 [BukkitParticles] 转换为 NMS 中的 Particles 类型
     */
    fun adapt(particles: BukkitParticles): Any

    /**
     * 将 int 转换为 TropicalFish.Pattern 类型
     */
    fun adaptTropicalFishPattern(data: Int): TropicalFish.Pattern

    /**
     * 将 TropicalFish.Pattern 转换为 int 类型
     */
    fun adaptTropicalFishPattern(pattern: TropicalFish.Pattern): Int

    /**
     * 通过 id 获取实体
     */
    fun getEntity(world: World, id: Int): Entity?

    /**
     * 获取给定实体的 DataWatcher
     */
    fun getEntityDataWatcher(entity: Entity): Any

    /**
     * 获取方块序号
     */
    fun getBlockId(materialData: MaterialData): Int

    /**
     * 将 Vec3d 转换为 Vector 类型
     */
    fun vec3dToVector(vec3d: Any): Vector

    /**
     * 使用 ChatSerializer 将 IChatBaseComponent 转换为 String 类型
     */
    fun craftChatSerializerToJson(compound: Any): String

    /**
     * 使用 CraftChatMessage 将字符串转换为 IChatBaseComponent 类型
     */
    fun craftChatMessageFromString(message: String): Any

    /**
     * 玩家是否正在观察某区块
     */
    fun isChunkVisible(player: Player, chunkX: Int, chunkZ: Int): Boolean
}