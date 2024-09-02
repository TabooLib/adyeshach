package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyDisplay
import ink.ptms.adyeshach.impl.util.ifTrue
import ink.ptms.adyeshach.impl.util.toRGB
import org.bukkit.entity.Display
import taboolib.common5.cint

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultDisplay
 *
 * @author 坏黑
 * @since 2023/1/10 00:25
 */
@Suppress("SpellCheckingInspection")
abstract class DefaultDisplay(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyDisplay {

    override fun setCustomMeta(key: String, value: String?): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "glowcoloroverride", "glow_color_override" -> {
                // 对 RGB 写法进行兼容
                if (value != null && value.contains(',')) {
                    setGlowColorOverride(value.toRGB())
                    true
                } else {
                    false
                }
            }
            "brightnessoverride", "brightness_override" -> {
                if (value != null) {
                    val args = value.split(',')
                    setBrightnessOverride(Display.Brightness(args.getOrNull(0).cint, args.getOrNull(1).cint))
                } else {
                    setBrightnessOverride(null)
                }
                true
            }
            else -> false
        }
    }
}