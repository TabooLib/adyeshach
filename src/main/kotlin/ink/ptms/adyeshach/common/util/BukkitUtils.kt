package ink.ptms.adyeshach.common.util

import org.bukkit.DyeColor
import org.bukkit.entity.*

/**
 * @author Arasple
 * @date 2020/8/19 21:25
 */
object BukkitUtils {

    fun valuesDyeColor(): Array<DyeColor> {
        return DyeColor.values()
    }

    @Suppress("DEPRECATION")
    fun valuesHorseVariant(): Array<Horse.Variant> {
        return Horse.Variant.values()
    }

    fun valuesHorseColor(): Array<Horse.Color> {
        return Horse.Color.values()
    }

    fun valuesHorseStyle(): Array<Horse.Style> {
        return Horse.Style.values()
    }

    fun valuesCatType(): Array<Cat.Type> {
        return Cat.Type.values()
    }

    fun valuesLlamaColor(): Array<Llama.Color> {
        return Llama.Color.values()
    }

    fun valuesRabbitType(): Array<Rabbit.Type> {
        return Rabbit.Type.values()
    }

    fun valuesParrotVariant(): Array<Parrot.Variant> {
        return Parrot.Variant.values()
    }

    fun valuesIllagerSpell(): Array<Spellcaster.Spell> {
        return Spellcaster.Spell.values()
    }

}