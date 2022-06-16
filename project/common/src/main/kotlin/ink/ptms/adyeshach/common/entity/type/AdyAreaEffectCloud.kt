package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import org.bukkit.Color

/**
 * @author sky
 * @since 2020-08-04 18:31
 */
abstract class AdyAreaEffectCloud : AdyEntity() {

    open fun setRadius(radius: Float) {
        setMetadata("radius", radius)
    }

    open fun getRadius(): Float {
        return getMetadata("radius")
    }

    open fun setColor(color: Color) {
        setMetadata("color", color.asRGB())
    }

    open fun getColor(): Color {
        return Color.fromRGB(getMetadata<Int>("color").coerceAtLeast(0))
    }

    open fun setIgnoreRadius(ignoreRadius: Boolean) {
        setMetadata("ignoreRadius", ignoreRadius)
    }

    open fun isIgnoreRadius(): Boolean {
        return getMetadata("ignoreRadius")
    }

    open fun setParticle(particle: BukkitParticles) {
        setMetadata("particle", particle)
    }

    open fun getParticle(): BukkitParticles {
        return getMetadata("particle")
    }
}