package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.entity.EntityThrowable
import org.bukkit.Color

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
interface AdyArrow : AdyEntity, EntityThrowable {

    fun setCritical(value: Boolean) {
        setMetadata("isCritical", value)
    }

    fun isCritical(): Boolean {
        return getMetadata("isCritical")
    }

    fun setNoclip(value: Boolean) {
        setMetadata("noclip", value)
    }

    fun isNoclip(): Boolean {
        return getMetadata("noclip")
    }

    fun setPiercingLevel(value: Byte) {
        setMetadata("piercingLevel", value)
    }

    fun getPiercingLevel(): Byte {
        return getMetadata("piercingLevel")
    }

    fun setColor(value: Color) {
        setMetadata("color", value.asRGB())
    }

    fun getColor(): Color {
        return Color.fromRGB(getMetadata("color"))
    }
}