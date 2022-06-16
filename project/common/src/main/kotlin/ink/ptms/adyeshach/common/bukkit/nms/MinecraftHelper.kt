package ink.ptms.adyeshach.common.bukkit.nms

import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.TropicalFish
import org.bukkit.material.MaterialData
import org.bukkit.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.bukkit.nms.MinecraftHelper
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
    fun adapt(type: Location): Any

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
}