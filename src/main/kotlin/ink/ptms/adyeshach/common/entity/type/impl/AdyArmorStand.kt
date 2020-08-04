package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.util.EulerAngle

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyArmorStand(owner: Player) : AdyHumanLike(owner, EntityTypes.ARMOR_STAND), MetadataExtend {

    init {
        properties = ArmorStandProperties()
    }

    fun setSmall(value: Boolean) {
        getProperties().small = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(14, baseData()))
    }

    fun isSmall(): Boolean {
        return getProperties().small
    }

    fun setArms(value: Boolean) {
        getProperties().hasArms = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(14, baseData()))
    }

    fun hasArms(): Boolean {
        return getProperties().hasArms
    }

    fun baseData(): Byte {
        return getProperties().run {
            var bits = 0
            if (small) bits += 0x01
            if (hasArms) bits += 0x04
            if (!hasBasePlate) bits += 0x08
            if (market) bits += 0x10
            bits.toByte()
        }
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    NMS.INSTANCE.getMetaEntityByte(14, baseData())
            )
        }
    }

    private fun getProperties(): ArmorStandProperties = properties as ArmorStandProperties

    private class ArmorStandProperties : EntityProperties() {

        @Expose
        var small = false

        @Expose
        var hasArms = false

        @Expose
        var hasBasePlate = true

        @Expose
        var market = false

        @Expose
        var rotation = mapOf(
                ArmorStandRotation.HEAD to EulerAngle(0.0, 0.0, 0.0),
                ArmorStandRotation.BODY to EulerAngle(0.0, 0.0, 0.0),
                ArmorStandRotation.LEFT_ARM to EulerAngle(-10.0, 0.0, -10.0),
                ArmorStandRotation.RIGHT_ARM to EulerAngle(-15.0, 0.0, 10.0),
                ArmorStandRotation.LEFT_LEG to EulerAngle(-1.0, 0.0, -1.0),
                ArmorStandRotation.RIGHT_LEG to EulerAngle(1.0, 0.0, 1.0)
        )
    }

    enum class ArmorStandRotation {

        HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG
    }
}