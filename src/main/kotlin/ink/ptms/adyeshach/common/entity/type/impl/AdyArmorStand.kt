package ink.ptms.adyeshach.common.entity.type.impl

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
class AdyArmorStand(owner: Player) : AdyEntityLiving(owner, EntityTypes.ARMOR_STAND) {

    init {
        registerMetaByteMask(14, "isSmall", 0x01)
        registerMetaByteMask(14, "hasArms", 0x04)
        registerMetaByteMask(14, "noBasePlate", 0x08)
        registerMetaByteMask(14, "isMarker", 0x10)
        registerMeta(15, "angleHead", EulerAngle(0.0, 0.0, 0.0))
        registerMeta(16, "angleBody", EulerAngle(0.0, 0.0, 0.0))
        registerMeta(17, "angleLeftArm", EulerAngle(-10.0, 0.0, -10.0))
        registerMeta(18, "angleRightArm", EulerAngle(-15.0, 0.0, 10.0))
        registerMeta(19, "angleLeftLeg", EulerAngle(-1.0, 0.0, -1.0))
        registerMeta(20, "angleRightLeg", EulerAngle(1.0, 0.0, 1.0))
    }

    fun setSmall(value: Boolean) {
        setMetadata("isSmall", value)
    }

    fun isSmall(): Boolean {
        return getMetadata("isSmall")
    }

    fun setArms(value: Boolean) {
        setMetadata("hasArms", value)
    }

    fun hasArms(): Boolean {
        return getMetadata("hasArms")
    }

    fun setBasePlate(value: Boolean) {
        setMetadata("noBasePlate", !value)
    }

    fun hasBasePlate(): Boolean {
        return getMetadata("noBasePlate")
    }

    fun setMarker(value: Boolean) {
        setMetadata("isMarker", value)
    }

    fun isMarker(): Boolean {
        return getMetadata("isMarker")
    }

    fun setRotation(rotation: EntityRotation, eulerAngle: EulerAngle) {
        setMetadata(rotation.index, eulerAngle)
    }

    fun getRotation(rotation: EntityRotation): EulerAngle {
        return getMetadata(rotation.index)
    }
}