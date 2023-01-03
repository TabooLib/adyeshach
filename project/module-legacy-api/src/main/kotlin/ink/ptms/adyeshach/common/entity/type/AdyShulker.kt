package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.data.VectorNull

import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.DyeColor
import org.bukkit.util.Vector

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyShulker(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyMob(EntityTypes.SHULKER, v2) {

    init {
        testing = true
    }

    fun setAttachFace(value: BukkitDirection) {
        setMetadata("attachFace", value.ordinal)
    }

    fun getAttachFace(): BukkitDirection {
        return BukkitDirection.of(getMetadata("attackFace"))
    }

    fun setAttachPosition(position: Vector?) {
        setMetadata("attachPosition", position ?: VectorNull())
    }

    fun getAttachPosition(): Vector? {
        val position = getMetadata<Vector>("attachPosition")
        return if (position is VectorNull) null else position
    }

    fun setShieldHeight(value: Byte) {
        setMetadata("shieldHeight", value)
    }

    fun getShieldHeight(): Byte {
        return getMetadata("shieldHeight")
    }

    fun setColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    fun setColor(): DyeColor {
        return DyeColor.values()[getMetadata("color")]
    }
}