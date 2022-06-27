package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.MinecraftHelper
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.TropicalFish
import org.bukkit.material.MaterialData
import org.bukkit.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftHelper
 *
 * @author 坏黑
 * @since 2022/6/28 00:07
 */
class DefaultMinecraftHelper : MinecraftHelper {

    override fun adapt(type: EntityTypes): Any {
        TODO("Not yet implemented")
    }

    override fun adapt(type: Location): Any {
        TODO("Not yet implemented")
    }

    override fun adapt(paintings: BukkitPaintings): Any {
        TODO("Not yet implemented")
    }

    override fun adapt(particles: BukkitParticles): Any {
        TODO("Not yet implemented")
    }

    override fun adaptTropicalFishPattern(data: Int): TropicalFish.Pattern {
        TODO("Not yet implemented")
    }

    override fun adaptTropicalFishPattern(pattern: TropicalFish.Pattern): Int {
        TODO("Not yet implemented")
    }

    override fun getEntity(world: World, id: Int): Entity? {
        TODO("Not yet implemented")
    }

    override fun getEntityDataWatcher(entity: Entity): Any {
        TODO("Not yet implemented")
    }

    override fun getBlockId(materialData: MaterialData): Int {
        TODO("Not yet implemented")
    }

    override fun vec3dToVector(vec3d: Any): Vector {
        TODO("Not yet implemented")
    }
}