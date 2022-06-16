package ink.ptms.adyeshach.common.entity.type

import org.bukkit.DyeColor
import org.bukkit.entity.Llama

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyLlama : AdyHorseChested() {

    open fun setCarpetColor(dyeColor: DyeColor?) {
        setMetadata("carpetColor", dyeColor?.ordinal ?: -1)
    }

    open fun setCarpetColor(): DyeColor? {
        val color = getMetadata<Int>("carpetColor")
        return if (color == -1) null else DyeColor.values()[color]
    }

    open fun setType(type: Llama.Color) {
        setMetadata("carpetColor", type.ordinal)
    }

    open fun getType(): Llama.Color {
        return Llama.Color.values()[getMetadata("color")]
    }
}