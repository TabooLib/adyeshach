package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.element.EntityRotation
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import org.bukkit.util.EulerAngle

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyArmorStand() : AdyEntityLiving(EntityTypes.ARMOR_STAND) {

    init {
        /**
         * 1.15 完全一致
         * 1.14, Indexs 依次对应 13-19
         * 1.13/1.12/1.11/1.10, Indexs 依次对应 11-17
         *
         * 1.9 依次对应 10-16,
         *   其中 Index=10 有额外属性 hasGravity 0x02
         */
        registerMetaByteMask(at(11600.to(14), 11400.to(13), 11100.to(11), 10900.to(10)), "isSmall", 0x01)
        registerMetaByteMask(at(11600.to(14), 11400.to(13), 11100.to(11), 10900.to(10)), "hasArms", 0x04)
        registerMetaByteMask(at(11600.to(14), 11400.to(13), 11100.to(11), 10900.to(10)), "noBasePlate", 0x08)
        registerMetaByteMask(at(11600.to(14), 11400.to(13), 11100.to(11), 10900.to(10)), "isMarker", 0x10)
        registerMetaByteMask(at(11000.to(-1), 10900.to(10)), "hasGravity", 0x02)
        registerMeta(at(11600.to(15), 11400.to(14), 11100.to(12), 10900.to(11)), "angleHead", EulerAngle(0.0, 0.0, 0.0))
        registerMeta(at(11600.to(16), 11400.to(15), 11100.to(13), 10900.to(12)), "angleBody", EulerAngle(0.0, 0.0, 0.0))
        registerMeta(at(11600.to(17), 11400.to(16), 11100.to(14), 10900.to(13)), "angleLeftArm", EulerAngle(-10.0, 0.0, -10.0))
        registerMeta(at(11600.to(18), 11400.to(17), 11100.to(15), 10900.to(14)), "angleRightArm", EulerAngle(-15.0, 0.0, 10.0))
        registerMeta(at(11600.to(19), 11400.to(18), 11100.to(16), 10900.to(15)), "angleLeftLeg", EulerAngle(-1.0, 0.0, -1.0))
        registerMeta(at(11600.to(20), 11400.to(19), 11100.to(17), 10900.to(16)), "angleRightLeg", EulerAngle(1.0, 0.0, 1.0))
    }

    fun setSmall(value: Boolean) {
        setMetadata("isSmall", value)
    }

    fun isSmall(): Boolean {
        return getMetadata("isSmall")
    }

    fun setGravity(value: Boolean) {
        setMetadata("hasGravity", value)
    }

    fun isGravity(): Boolean {
        return getMetadata("hasGravity")
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