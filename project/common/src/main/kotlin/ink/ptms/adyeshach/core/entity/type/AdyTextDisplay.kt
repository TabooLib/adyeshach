package ink.ptms.adyeshach.core.entity.type

import ink.ptms.adyeshach.core.bukkit.data.BukkitTextAlignment
import ink.ptms.adyeshach.core.util.BukkitColor
import ink.ptms.adyeshach.core.util.JavaColor
import ink.ptms.adyeshach.core.util.argb
import taboolib.module.chat.ComponentText
import kotlin.math.roundToInt

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

    fun unsetBackgroundColor() {
        setBackgroundColor(JavaColor(0, 0, 0, 0))
    }

    fun setBackgroundColor(value: BukkitColor) {
        if (value.red == 0 && value.green == 0 && value.blue == 0) {
            unsetBackgroundColor()
        } else {
            setMetadata("backgroundColor", argb(255, value.red, value.green, value.blue))
        }
    }

    fun setBackgroundColor(value: JavaColor) {
        setMetadata("backgroundColor", argb(value.alpha, value.red, value.green, value.blue))
    }

    fun getBackgroundColor(): JavaColor {
        return JavaColor(getMetadata<Int>("backgroundColor").let { if (it == -1) 0 else it }, true)
    }

    fun setTextOpacity(percent: Double) {
        // Ensure the percent is within the range 0 to 1
        val clampedPercent = percent.coerceIn(0.0, 1.0)
        // Convert percent to an opacity value from 0 to 255
        var opacity = (clampedPercent * 255).roundToInt()
        // Apply the Minecraft specific transformation
        if (opacity >= 128) {
            // For values >= 128, use <opacity> - 256
            opacity -= 256
        }
        // Ensure the value is within the range of a byte (-128 to 127)
        setTextOpacity(opacity.toByte())
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