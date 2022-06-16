package ink.ptms.adyeshach.common.entity.type

import org.bukkit.material.MaterialData

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyMinecart : AdyEntity() {

    /**
     * 设置自定义方块
     */
    abstract fun setCustomBlock(materialData: MaterialData)

    /**
     * 获取自定义方块
     */
    abstract fun getCustomBlock(): MaterialData

    open fun getCustomBlockOffset(): Int {
        return getMetadata("customBlockPosition")
    }

    open fun setCustomBlockOffset(value: Int) {
        setMetadata("customBlockPosition", value)
    }

    open fun isShowCustomBlock(): Boolean {
        return getMetadata("showCustomBlock")
    }

    open fun setShowCustomBlock(value: Boolean) {
        setMetadata("showCustomBlock", value)
    }
}