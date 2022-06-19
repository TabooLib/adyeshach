package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitRotation
import org.bukkit.util.EulerAngle

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
interface AdyArmorStand : AdyEntityLiving {

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

    fun setRotation(rotation: BukkitRotation, eulerAngle: EulerAngle) {
        setMetadata(rotation.metaName, eulerAngle)
    }

    fun getRotation(rotation: BukkitRotation): EulerAngle {
        return getMetadata(rotation.metaName)
    }
}