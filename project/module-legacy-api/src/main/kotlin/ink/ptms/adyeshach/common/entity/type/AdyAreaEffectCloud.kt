package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.core.util.getEnumOrNull
import org.bukkit.Color

/**
 * 仅 1.16 有属性
 *
 * @author sky
 * @since 2020-08-04 18:31
 */
@Deprecated("Outdated but usable")
class AdyAreaEffectCloud(v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntity(EntityTypes.AREA_EFFECT_CLOUD, v2) {

    fun setRadius(radius: Float) {
        setMetadata("radius", radius)
    }

    fun getRadius(): Float {
        return getMetadata("radius")
    }

    fun setColor(color: Color) {
        setMetadata("color", color.asRGB())
    }

    fun getColor(): Color {
        return Color.fromRGB(getMetadata<Int>("color").let { if (it == -1) 0 else it })
    }

    fun setIgnoreRadius(ignoreRadius: Boolean) {
        setMetadata("ignoreRadius", ignoreRadius)
    }

    fun isIgnoreRadius(): Boolean {
        return getMetadata("ignoreRadius")
    }

    fun setParticle(particle: BukkitParticles) {
        val v2 = ink.ptms.adyeshach.core.bukkit.BukkitParticles::class.java.getEnumOrNull(particle.name) ?: ink.ptms.adyeshach.core.bukkit.BukkitParticles.FLAME
        setMetadata("particle", v2)
    }

    fun getParticle(): BukkitParticles {
        val v2 = getMetadata<ink.ptms.adyeshach.core.bukkit.BukkitParticles>("particle")
        return BukkitParticles::class.java.getEnumOrNull(v2.name) ?: BukkitParticles.FLAME
    }
}