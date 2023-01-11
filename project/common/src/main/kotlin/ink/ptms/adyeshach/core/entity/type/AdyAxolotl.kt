package ink.ptms.adyeshach.core.entity.type

import org.bukkit.entity.Axolotl

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
interface AdyAxolotl : AdyMob {

    fun setColor(value: Axolotl.Variant) {
        setMetadata("color", value.ordinal)
    }

    fun getColor(): Axolotl.Variant {
        return Axolotl.Variant.values()[getMetadata("color")]
    }
}