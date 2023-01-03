package ink.ptms.adyeshach.common.entity.type


import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.DyeColor
import org.bukkit.entity.Llama

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
open class AdyLlama(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyHorseChested(entityTypes, v2) {

    constructor(v2: ink.ptms.adyeshach.core.entity.EntityInstance): this(EntityTypes.LLAMA, v2)

    fun setCarpetColor(dyeColor: DyeColor?) {
        setMetadata("carpetColor", dyeColor?.ordinal ?: -1)
    }

    fun setCarpetColor(): DyeColor? {
        val color = getMetadata<Int>("carpetColor")
        return if (color == -1) null else DyeColor.values()[color]
    }

    fun setType(type: Llama.Color) {
        setMetadata("carpetColor", type.ordinal)
    }

    fun getType(): Llama.Color {
        return Llama.Color.values()[getMetadata("color")]
    }
}