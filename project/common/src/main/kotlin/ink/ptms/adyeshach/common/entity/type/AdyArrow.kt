package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityThrowable
import org.bukkit.Color

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
@Suppress("SpellCheckingInspection")
abstract class AdyArrow : AdyEntity(), EntityThrowable {

    open fun setCritical(value: Boolean) {
        setMetadata("isCritical", value)
    }

    open fun isCritical(): Boolean {
        return getMetadata("isCritical")
    }

    open fun setNoclip(value: Boolean) {
        setMetadata("noclip", value)
    }

    open fun isNoclip(): Boolean {
        return getMetadata("noclip")
    }

    open fun setPiercingLevel(value: Byte) {
        setMetadata("piercingLevel", value)
    }

    open fun getPiercingLevel(): Byte {
        return getMetadata("piercingLevel")
    }

    open fun setColor(value: Color) {
        setMetadata("color", value.asRGB())
    }

    open fun getColor(): Color {
        return Color.fromRGB(getMetadata("color"))
    }
}