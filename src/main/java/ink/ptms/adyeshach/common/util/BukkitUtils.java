package ink.ptms.adyeshach.common.util;

import org.bukkit.DyeColor;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;

/**
 * 求你们不要改这个类，用 java 写是有原因的。
 *
 * @author sky
 * @since 2020-08-05 16:53
 */
public class BukkitUtils {

    public static DyeColor[] valuesDyeColor() {
        return DyeColor.values();
    }

    public static Cat.Type[] valuesCatType() {
        return Cat.Type.values();
    }

    public static Fox.Type[] valuesFoxType() {
        return Fox.Type.values();
    }

    public static Llama.Color[] valuesLlamaColor() {
        return Llama.Color.values();
    }

    public static Rabbit.Type[] valuesRabbitType() {
        return Rabbit.Type.values();
    }

    public static Parrot.Variant[] valuesParrotVariant() {
        return Parrot.Variant.values();
    }

    public static Spellcaster.Spell[] valuesIllagerSpell() {
        return Spellcaster.Spell.values();
    }

    public static EquipmentSlot[] valuesEquipmentSlot() {
        return EquipmentSlot.values();
    }
}