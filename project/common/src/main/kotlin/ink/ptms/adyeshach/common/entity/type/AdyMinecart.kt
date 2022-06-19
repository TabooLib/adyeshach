package ink.ptms.adyeshach.common.entity.type

import org.bukkit.material.MaterialData

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyMinecart : AdyEntity {

    /**
     * 设置自定义方块
     */
    fun setCustomBlock(materialData: MaterialData)

    /**
     * 获取自定义方块
     */
    fun getCustomBlock(): MaterialData

    fun getCustomBlockOffset(): Int {
        return getMetadata("customBlockPosition")
    }

    fun setCustomBlockOffset(value: Int) {
        setMetadata("customBlockPosition", value)
    }

    fun isShowCustomBlock(): Boolean {
        return getMetadata("showCustomBlock")
    }

    fun setShowCustomBlock(value: Boolean) {
        setMetadata("showCustomBlock", value)
    }
}