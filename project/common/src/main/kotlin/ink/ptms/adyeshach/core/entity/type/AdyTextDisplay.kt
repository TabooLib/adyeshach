package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.bukkit.data.BukkitTextAlignment
import ink.ptms.adyeshach.core.util.BukkitColor
import ink.ptms.adyeshach.core.util.JavaColor
import ink.ptms.adyeshach.core.util.argb
import org.bukkit.Color
import taboolib.module.chat.ComponentText

/**
 * @author sky
 * @date 2023/4/5 22:18
 */
interface AdyTextDisplay : AdyDisplay {

    fun setText(value: ComponentText)

    fun getText(): ComponentText

    fun setLineWidth(value: Int) {
        setMetadata("lineWidth", value)
    }

    fun getLineWidth(): Int {
        return getMetadata("lineWidth")
    }

    fun setBackgroundColor(value: BukkitColor) {
        setMetadata("backgroundColor", argb(255, value.red, value.green, value.blue))
    }

    fun setBackgroundColor(value: JavaColor) {
        setMetadata("backgroundColor", argb(value.alpha, value.red, value.green, value.blue))
    }

    fun getBackgroundColor(): JavaColor {
        return JavaColor(getMetadata<Int>("backgroundColor").let { if (it == -1) 0 else it }, true)
    }

    fun setTextOpacity(value: Byte) {
        setMetadata("textOpacity", value)
    }

    fun getTextOpacity(): Byte {
        return getMetadata("textOpacity")
    }

    fun setShadow(value: Boolean) {
        setMetadata("shadow", value)
    }

    fun isShadow(): Boolean {
        return getMetadata("shadow")
    }

    fun setSeeThrough(value: Boolean) {
        setMetadata("seeThrough", value)
    }

    fun isSeeThrough(): Boolean {
        return getMetadata("seeThrough")
    }

    fun setUseDefaultBackgroundColor(value: Boolean) {
        setMetadata("useDefaultBackgroundColor", value)
    }

    fun isUseDefaultBackgroundColor(): Boolean {
        return getMetadata("useDefaultBackgroundColor")
    }

    fun setAlignment(value: BukkitTextAlignment)

    fun getAlignment(): BukkitTextAlignment
}