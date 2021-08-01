package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.DyeColor
import org.bukkit.entity.Cat

/**
 * @Author sky
 * @Since 2020-08-04 19:30
 */
class AdyCat : AdyEntityTameable(EntityTypes.CAT) {

    /**
     * 仅 1.16, 1.15 有属性
     */
    init {
        registerMeta(at(11700 to 19, 11500 to 18), "type", Cat.Type.TABBY.ordinal)
                .from(Editors.enums(Cat.Type::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
                .display { _, entity, _ ->
                    Cat.Type.values()[entity.getMetadata("type")].name
                }.build()
        registerMeta(at(11700 to 20), "isLying", false)
        registerMeta(at(11700 to 21), "isRelaxed", false)
        registerMeta(at(11700 to 22, 11500 to 21), "color", DyeColor.RED.ordinal)
                .from(Editors.enums(DyeColor::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
                .display { _, entity, _ ->
                    DyeColor.values()[entity.getMetadata("color")].name
                }.build()
    }

    fun setType(value: Cat.Type) {
        setMetadata("type", value.ordinal)
    }

    fun getType(): Cat.Type {
        return Cat.Type.values()[getMetadata("type")]
    }

    fun setCollarColor(value: DyeColor) {
        setMetadata("color", value.ordinal)
    }

    fun getCollarColor(): DyeColor {
        return DyeColor.values()[getMetadata("color")]
    }

    var isLying: Boolean
        get() = getMetadata("isLying")
        set(value) {
            setMetadata("isLying", value)
        }

    var isRelaxed: Boolean
        get() = getMetadata("isRelaxed")
        set(value) {
            setMetadata("isRelaxed", value)
        }
}