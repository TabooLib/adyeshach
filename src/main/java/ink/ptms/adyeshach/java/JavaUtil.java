package ink.ptms.adyeshach.java;

import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Spellcaster;

/**
 * @Author sky
 * @Since 2020-08-05 16:53
 */
public class JavaUtil {

    public static DyeColor[] valuesDyeColor() {
        return DyeColor.values();
    }

    public static Cat.Type[] valuesCatType() {
        return Cat.Type.values();
    }

    public static Llama.Color[] valuesLlamaColor() {
        return Llama.Color.values();
    }

    public static Spellcaster.Spell[] valueIllagerSpell() {
        return Spellcaster.Spell.values();
    }
}
