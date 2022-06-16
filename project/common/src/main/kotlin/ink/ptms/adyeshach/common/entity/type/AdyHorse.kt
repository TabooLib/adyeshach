package ink.ptms.adyeshach.common.entity.type

import org.bukkit.entity.Horse

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyHorse : AdyHorseBase() {

    /**
     * 设置马的颜色与花纹
     */
    abstract fun setColorAndStyle(color: Horse.Color, style: Horse.Style)

    /**
     * 设置马的颜色
     */
    abstract fun setColor(color: Horse.Color)

    /**
     * 设置马的花纹
     */
    abstract fun setStyle(style: Horse.Style)

    /**
     * 获取马的颜色
     */
    abstract fun getColor(): Horse.Color

    /**
     * 获取马的花纹
     */
    abstract fun getStyle(): Horse.Style

    open fun getVariant(): Int {
        return getMetadata("variant")
    }

    open fun setVariant(variant: Int) {
        setMetadata("variant", variant)
    }
}