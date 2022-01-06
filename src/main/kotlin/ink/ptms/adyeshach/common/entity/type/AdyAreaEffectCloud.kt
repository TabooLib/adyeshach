package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Color

/**
 * 仅 1.16 有属性
 *
 * @author sky
 * @since 2020-08-04 18:31
 */
class AdyAreaEffectCloud : AdyEntity(EntityTypes.AREA_EFFECT_CLOUD) {

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
        setMetadata("particle", particle)
    }

    fun getParticle(): BukkitParticles {
        return getMetadata("particle")
    }
}