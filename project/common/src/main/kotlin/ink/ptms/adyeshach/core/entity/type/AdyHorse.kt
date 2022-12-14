package ink.ptms.adyeshach.core.entity.type

import org.bukkit.entity.Horse

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyHorse : AdyHorseBase {

    /**
     * 设置马的颜色与花纹
     */
    fun setColorAndStyle(color: Horse.Color, style: Horse.Style)

    /**
     * 设置马的颜色
     */
    fun setColor(color: Horse.Color)

    /**
     * 设置马的花纹
     */
    fun setStyle(style: Horse.Style)

    /**
     * 获取马的颜色
     */
    fun getColor(): Horse.Color

    /**
     * 获取马的花纹
     */
    fun getStyle(): Horse.Style

    fun getVariant(): Int {
        return getMetadata("variant")
    }

    fun setVariant(variant: Int) {
        setMetadata("variant", variant)
    }
}