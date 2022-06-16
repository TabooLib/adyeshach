package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.data.EmptyVector
import org.bukkit.DyeColor
import org.bukkit.util.Vector

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyShulker : AdyMob() {

    open fun setAttachFace(value: BukkitDirection) {
        setMetadata("attachFace", value.ordinal)
    }

    open fun getAttachFace(): BukkitDirection {
        return BukkitDirection.of(getMetadata("attackFace"))
    }

    open fun setAttachPosition(position: Vector?) {
        setMetadata("attachPosition", position ?: EmptyVector())
    }

    open fun getAttachPosition(): Vector? {
        return getMetadata<Vector>("attachPosition").takeUnless { it is EmptyVector }
    }

    open fun setShieldHeight(value: Byte) {
        setMetadata("shieldHeight", value)
    }

    open fun getShieldHeight(): Byte {
        return getMetadata("shieldHeight")
    }

    open fun setColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    open fun setColor(): DyeColor {
        return DyeColor.values()[getMetadata("color")]
    }
}