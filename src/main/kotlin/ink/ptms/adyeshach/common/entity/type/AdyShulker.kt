package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.element.NullPosition
import ink.ptms.adyeshach.common.util.BukkitUtils
import io.izzel.taboolib.module.nms.impl.Position
import org.bukkit.DyeColor

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyShulker() : AdyMob(EntityTypes.SHULKER) {

    init {
        /*
        1.16,1.15
        15 ->Attach face
        16 ->Attachment position
        17 ->Shield height
        18 ->Color (dye color)
        1.14
        14 ->Attach face
        15 ->Attachment position
        16 ->Shield height
        17 ->Color (dye color)
        1.13,1.12,1.11
        12 ->Attach face
        13 ->Attachment position
        14 ->Shield height
        15 ->Color (dye color)
        1.10
        12 ->Facing direction
        13 ->Attachment position
        14 ->Shield height
        1.9
        11 ->Facing direction
        12 ->Attachment position
        13 ->Shield height
         */
        registerMeta(at(11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "attachFace", BukkitDirection.DOWN.ordinal)
        registerMeta(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "attachPosition", NullPosition())
        registerMeta(at(11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "shieldHeight", 0.toByte())
        registerMeta(at(11500 to 18, 11400 to 17, 11100 to 15), "color", DyeColor.PURPLE.ordinal)
    }

    fun setAttachFace(value: BukkitDirection) {
        setMetadata("attachFace", value.ordinal)
    }

    fun getAttachFace(): BukkitDirection {
        return BukkitDirection.of(getMetadata("attackFace"))
    }

    fun setAttachPosition(position: Position?) {
        setMetadata("attachPosition", position ?: NullPosition())
    }

    fun getAttachPosition(): Position? {
        val position = getMetadata<Position>("attachPosition")
        return if (position is NullPosition) null else position
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
        return BukkitUtils.valuesDyeColor()[getMetadata("color")]
    }
}