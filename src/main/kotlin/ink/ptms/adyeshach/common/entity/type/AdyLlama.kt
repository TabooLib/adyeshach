package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.BukkitUtils
import org.bukkit.DyeColor
import org.bukkit.entity.Fox
import org.bukkit.entity.Llama

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdyLlama(entityTypes: EntityTypes) : AdyHorseChested(entityTypes) {

    constructor(): this(EntityTypes.LLAMA)

    init {
        /*
        1.16,1.15
        19 ->Strength (number of columns of 3 slots in the llama's inventory once a chest is equipped)
        20 ->Carpet color (a dye color, or -1 if no carpet equipped)
        21 ->Variant (0: llama_creamy.png, 1: llama_white.png, 2: llama_brown.png, 3: llama_gray.png)
        1.14
        18 ->Strength (number of columns of 3 slots in the llama's inventory once a chest is equipped)
        19 ->Carpet color (a dye color, or -1 if no carpet equipped)
        20 ->Variant (0: llama_creamy.png, 1: llama_white.png, 2: llama_brown.png, 3: llama_gray.png)
        1.13,1.12,1.11
        16 ->Strength (number of columns of 3 slots in the llama's inventory once a chest is equipped)
        17 ->Carpet color (a dye color, or -1 if no carpet equipped)
        18 ->Variant (0: llama_creamy.png, 1: llama_white.png, 2: llama_brown.png, 3: llama_gray.png)
        1.10,1.9
        null
         */
        registerMeta(at(11500 to 20, 11400 to 19, 11100 to 17), "carpetColor", -1)
                .from(Editors.enums(DyeColor::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
                .display { _, entity, _ ->
                    BukkitUtils.valuesDyeColor()[entity.getMetadata("carpetColor")].name
                }.build()
        registerMeta(at(11500 to 21, 11400 to 20, 11100 to 18), "color", Llama.Color.CREAMY.ordinal)
                .from(Editors.enums(Llama.Color::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
                .display { _, entity, _ ->
                    BukkitUtils.valuesLlamaColor()[entity.getMetadata("color")].name
                }.build()
    }

    fun setCarpetColor(dyeColor: DyeColor?) {
        setMetadata("carpetColor", dyeColor?.ordinal ?: -1)
    }

    fun setCarpetColor(): DyeColor? {
        val color = getMetadata<Int>("carpetColor")
        return if (color == -1) null else BukkitUtils.valuesDyeColor()[color]
    }

    fun setType(type: Llama.Color) {
        setMetadata("carpetColor", type.ordinal)
    }

    fun getType(): Llama.Color {
        return BukkitUtils.valuesLlamaColor()[getMetadata("color")]
    }
}