package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.BukkitUtils
import org.bukkit.DyeColor
import org.bukkit.entity.Cat

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyCat() : AdyEntityTameable(EntityTypes.CAT) {

    /**
     * 仅 1.16, 1.15 有属性
     */
    init {
        registerMeta(at(11500 to 18), "type", Cat.Type.TABBY.ordinal)
                .from(Editors.enums(Cat.Type::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
                .display { _, entity, _ ->
                    BukkitUtils.valuesCatType()[entity.getMetadata("type")].name
                }.build()
        registerMeta(at(11500 to 21), "color", DyeColor.RED.ordinal)
                .from(Editors.enums(DyeColor::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
                .display { _, entity, _ ->
                    BukkitUtils.valuesDyeColor()[entity.getMetadata("color")].name
                }.build()
    }

    fun setType(value: Cat.Type) {
        setMetadata("type", value.ordinal)
    }

    fun getType(): Cat.Type {
        return BukkitUtils.valuesCatType()[getMetadata("type")]
    }

    fun setCollarColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    fun getCollarColor(): DyeColor {
        return BukkitUtils.valuesDyeColor()[getMetadata("color")]
    }
}