package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdySheep
import ink.ptms.adyeshach.core.util.getEnum
import ink.ptms.adyeshach.impl.util.ifTrue
import org.bukkit.DyeColor
import taboolib.common5.cbool
import kotlin.experimental.and

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultSheep
 *
 * @author 坏黑
 * @since 2023/1/10 00:25
 */
@Suppress("SpellCheckingInspection")
abstract class DefaultSheep(entityTypes: EntityTypes) : DefaultEntityLiving(entityTypes), AdySheep {

    override fun getDyeColor(): DyeColor {
        return DyeColor.getByWoolData(getMetadata<Byte>("dyeColor") and 15) ?: DyeColor.WHITE
    }

    override fun setDyeColor(dyeColor: DyeColor) {
        val data = getMetadata<Byte>("dyeColor")
        setMetadata("dyeColor", (data.toInt() and 240 or (dyeColor.ordinal and 15)).toByte())
    }

    override fun isSheared(): Boolean {
        return getMetadata<Byte>("dyeColor").toInt() and 16 != 0
    }

    override fun setSheared(value: Boolean) {
        val data = getMetadata<Byte>("dyeColor")
        if (value) {
            setMetadata("dyeColor", (data.toInt() or 16).toByte())
        } else {
            setMetadata("dyeColor", (data.toInt() and -17).toByte())
        }
    }

    override fun setCustomMeta(key: String, value: String?): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "dyecolor", "dye_color" -> {
                setDyeColor(if (value != null) DyeColor::class.java.getEnum(value) else DyeColor.WHITE)
                true
            }
            "issheared", "is_sheared" -> {
                setSheared(value?.cbool ?: true)
                true
            }
            else -> false
        }
    }
}