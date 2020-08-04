package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Color
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 18:31
 */
class AdyAreaEffectCloud(owner: Player) : AdyEntity(owner, EntityTypes.AREA_EFFECT_CLOUD), MetadataExtend {

    init {
        properties = AreaEffectCloudProperties()
    }

    fun setRadius(radius: Float) {
        getProperties().radius = radius
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityFloat(7, radius))
    }

    fun getRadius(): Float {
        return getProperties().radius
    }

    fun setColor(color: Color) {
        getProperties().color = color.asRGB()
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityInt(8, color.asRGB()))
    }

    fun getColor(): Color {
        return Color.fromRGB(getProperties().color)
    }

    fun setIgnoreRadius(ignoreRadius: Boolean) {
        getProperties().ignoreRadius = ignoreRadius
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityBoolean(9, ignoreRadius))
    }

    fun isIgnoreRadius(): Boolean {
        return getProperties().ignoreRadius
    }

    fun setParticle(particle: BukkitParticles) {
        getProperties().particle = particle
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityParticle(10, particle))
    }

    fun getParticle(): BukkitParticles {
        return getProperties().particle
    }

    override fun metadata(): List<Any> {
        getProperties().run {
            return listOf(
                    NMS.INSTANCE.getMetaEntityFloat(7, radius),
                    NMS.INSTANCE.getMetaEntityInt(8, color),
                    NMS.INSTANCE.getMetaEntityBoolean(9, ignoreRadius),
                    NMS.INSTANCE.getMetaEntityParticle(10, particle)
            )
        }
    }

    private fun getProperties(): AreaEffectCloudProperties = properties as AreaEffectCloudProperties

    private class AreaEffectCloudProperties : EntityProperties() {

        @Expose
        var radius: Float = 0.5f

        @Expose
        var color: Int = 0

        @Expose
        var ignoreRadius = false

        @Expose
        var particle = BukkitParticles.EFFECT
    }
}