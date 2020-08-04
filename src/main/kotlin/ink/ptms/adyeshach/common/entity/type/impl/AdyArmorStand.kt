package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.element.EntityRotation
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player
import org.bukkit.util.EulerAngle

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyArmorStand(owner: Player) : AdyEntityLiving(owner, EntityTypes.ARMOR_STAND), MetadataExtend {

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

    fun setBasePlate(value: Boolean) {
        getProperties().hasBasePlate = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(14, baseData()))
    }

    fun hasBasePlate(): Boolean {
        return getProperties().hasBasePlate
    }

    fun setMarker(value: Boolean) {
        getProperties().marker = value
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityByte(14, baseData()))
    }

    fun isMarker(): Boolean {
        return getProperties().marker
    }

    fun setRotation(rotation: EntityRotation, eulerAngle: EulerAngle) {
        getProperties().rotation[rotation] = eulerAngle
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityVector(rotation.index, eulerAngle))
    }

    fun getRotation(rotation: EntityRotation): EulerAngle {
        val eulerAngle = getProperties().rotation[rotation] ?: EulerAngle.ZERO
        return EulerAngle(eulerAngle.x, eulerAngle.y, eulerAngle.z)
    }

    fun baseData(): Byte {
        return getProperties().run {
            var bits = 0
            if (small) bits += 0x01
            if (hasArms) bits += 0x04
            if (!hasBasePlate) bits += 0x08
            if (marker) bits += 0x10
            bits.toByte()
        }
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    NMS.INSTANCE.getMetaEntityByte(14, baseData()),
                    NMS.INSTANCE.getMetaEntityVector(15, getRotation(EntityRotation.HEAD)),
                    NMS.INSTANCE.getMetaEntityVector(16, getRotation(EntityRotation.BODY)),
                    NMS.INSTANCE.getMetaEntityVector(17, getRotation(EntityRotation.LEFT_ARM)),
                    NMS.INSTANCE.getMetaEntityVector(18, getRotation(EntityRotation.RIGHT_ARM)),
                    NMS.INSTANCE.getMetaEntityVector(19, getRotation(EntityRotation.LEFT_LEG)),
                    NMS.INSTANCE.getMetaEntityVector(20, getRotation(EntityRotation.RIGHT_LEG))
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
        var marker = false

        @Expose
        var rotation = hashMapOf(
                EntityRotation.HEAD to EulerAngle(0.0, 0.0, 0.0),
                EntityRotation.BODY to EulerAngle(0.0, 0.0, 0.0),
                EntityRotation.LEFT_ARM to EulerAngle(-10.0, 0.0, -10.0),
                EntityRotation.RIGHT_ARM to EulerAngle(-15.0, 0.0, 10.0),
                EntityRotation.LEFT_LEG to EulerAngle(-1.0, 0.0, -1.0),
                EntityRotation.RIGHT_LEG to EulerAngle(1.0, 0.0, 1.0)
        )
    }
}