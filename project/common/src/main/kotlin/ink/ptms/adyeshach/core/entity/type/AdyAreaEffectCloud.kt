package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.bukkit.BukkitParticles
import org.bukkit.Color

/**
 * @author sky
 * @since 2020-08-04 18:31
 */
interface AdyAreaEffectCloud : AdyEntity {

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
        return Color.fromRGB(getMetadata<Int>("color").coerceAtLeast(0))
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