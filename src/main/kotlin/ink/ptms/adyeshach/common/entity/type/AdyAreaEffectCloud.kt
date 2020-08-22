package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitProfession
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Color
import org.bukkit.entity.Villager

/**
 * @Author sky
 * @Since 2020-08-04 18:31
 */
class AdyAreaEffectCloud : AdyEntity(EntityTypes.AREA_EFFECT_CLOUD) {

    init {
        /**
         * 仅 1.16 有属性
         */
        registerMeta(at(11600 to 7), "radius", 0.5f)
        registerMeta(at(11600 to 8), "color", 0)
                .clone(Editors.COLOR)
                .build()
        registerMeta(at(11600 to 9), "ignoreRadius", false)
        registerMeta(at(11600 to 10), "particle", BukkitParticles.EFFECT)
                .clone(Editors.enums(BukkitParticles::class) { _, entity, meta, e -> "/adyeshachapi edit particle ${entity.uniqueId} ${meta.key} $e" })
                .onDisplay { _, entity, _ ->
                    entity.getMetadata<BukkitParticles>("particle").name
                }.build()
    }

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
        return Color.fromRGB(getMetadata("color"))
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