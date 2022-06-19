package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.data.EmptyVector
import org.bukkit.DyeColor
import org.bukkit.util.Vector

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyShulker : AdyMob {

    fun setAttachFace(value: BukkitDirection) {
        setMetadata("attachFace", value.ordinal)
    }

    fun getAttachFace(): BukkitDirection {
        return BukkitDirection.of(getMetadata("attackFace"))
    }

    fun setAttachPosition(position: Vector?) {
        setMetadata("attachPosition", position ?: EmptyVector())
    }

    fun getAttachPosition(): Vector? {
        return getMetadata<Vector>("attachPosition").takeUnless { it is EmptyVector }
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