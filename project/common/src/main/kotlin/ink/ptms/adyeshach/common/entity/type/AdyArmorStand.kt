package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitRotation
import org.bukkit.util.EulerAngle

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
abstract class AdyArmorStand : AdyEntityLiving() {

    open fun setSmall(value: Boolean) {
        setMetadata("isSmall", value)
    }

    open fun isSmall(): Boolean {
        return getMetadata("isSmall")
    }

    open fun setGravity(value: Boolean) {
        setMetadata("hasGravity", value)
    }

    open fun isGravity(): Boolean {
        return getMetadata("hasGravity")
    }

    open fun setArms(value: Boolean) {
        setMetadata("hasArms", value)
    }

    open fun hasArms(): Boolean {
        return getMetadata("hasArms")
    }

    open fun setBasePlate(value: Boolean) {
        setMetadata("noBasePlate", !value)
    }

    open fun hasBasePlate(): Boolean {
        return getMetadata("noBasePlate")
    }

    open fun setMarker(value: Boolean) {
        setMetadata("isMarker", value)
    }

    open fun isMarker(): Boolean {
        return getMetadata("isMarker")
    }

    open fun setRotation(rotation: BukkitRotation, eulerAngle: EulerAngle) {
        setMetadata(rotation.metaName, eulerAngle)
    }

    open fun getRotation(rotation: BukkitRotation): EulerAngle {
        return getMetadata(rotation.metaName)
    }
}