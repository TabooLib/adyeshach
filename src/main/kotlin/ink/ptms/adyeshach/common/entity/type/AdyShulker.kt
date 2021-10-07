package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.data.VectorNull
import ink.ptms.adyeshach.common.entity.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.DyeColor
import taboolib.common.util.Vector

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyShulker : AdyMob(EntityTypes.SHULKER) {

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
//        natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "attachFace", BukkitDirection.DOWN.ordinal)
//                .from(Editors.enums(BukkitDirection::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    BukkitDirection.values()[entity.getMetadata("attachFace")].name
//                }.build()
//        natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "attachPosition", VectorNull())
//        natural(at(11700 to 18, 11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "shieldHeight", 0.toByte())
//        natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11100 to 15), "color", DyeColor.PURPLE.ordinal)
//                .from(Editors.enums(DyeColor::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    DyeColor.values()[entity.getMetadata("color")].name
//                }.build()
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