package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitRotation
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.util.EulerAngle

/**
 * 1.15 完全一致
 * 1.14, Indexs 依次对应 13-19
 * 1.13/1.12/1.11/1.10, Indexs 依次对应 11-17
 *
 * 1.9 依次对应 10-16,
 *   其中 Index=10 有额外属性 hasGravity 0x02
 *
 * @author sky
 * @since 2020-08-04 19:30
 */
class AdyArmorStand : AdyEntityLiving(EntityTypes.ARMOR_STAND) {

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
        setMetadata(rotation.index, eulerAngle)
    }

    fun getRotation(rotation: BukkitRotation): EulerAngle {
        return getMetadata(rotation.index)
    }
}