package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Parrot

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyParrot : AdyEntityTameable(EntityTypes.PARROT) {

    init {
//        registerMeta(at(11700 to 19, 11500 to 18, 11400 to 17, 11200 to 15), "color", Parrot.Variant.RED.ordinal)
//                .from(Editors.enums(Parrot.Variant::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    Parrot.Variant.values()[entity.getMetadata("color")].name
//                }.build()
    }

    fun setColor(color: Parrot.Variant) {
        setMetadata("color", color.ordinal)
    }

    fun getColor(): Parrot.Variant {
        return Parrot.Variant.values()[getMetadata("color")]
    }
}
