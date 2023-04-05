package ink.ptms.adyeshach.core.entity.type

import org.bukkit.entity.TextDisplay
import taboolib.module.chat.ComponentText

/**
 * @author sky
 * @date 2020/8/4 22:18
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

    fun setBackgroundColor(value: Int) {
        setMetadata("backgroundColor", value)
    }

    fun getBackgroundColor(): Int {
        return getMetadata("backgroundColor")
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

    fun setAlignment(value: TextDisplay.TextAligment)

    fun getAlignment(): TextDisplay.TextAligment
}