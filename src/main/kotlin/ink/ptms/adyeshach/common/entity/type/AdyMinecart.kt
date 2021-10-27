package ink.ptms.adyeshach.common.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.material.MaterialData

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdyMinecart(entityTypes: EntityTypes) : AdyEntity(entityTypes) {

    constructor(): this(EntityTypes.MINECART)

    @Expose
    private var customBlock: Material = Material.AIR
    @Expose
    private var customBlockData: Byte = 0

    fun setCustomBlock(materialData: MaterialData) {
        customBlock = materialData.itemType
        customBlockData = materialData.data
        setMetadata("customBlock", NMS.INSTANCE.toBlockId(materialData))
    }

    fun getCustomBlock(): MaterialData {
        return MaterialData(customBlock, customBlockData)
    }

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