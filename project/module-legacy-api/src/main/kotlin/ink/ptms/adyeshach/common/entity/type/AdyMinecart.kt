package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyMinecart
import org.bukkit.material.MaterialData

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
open class AdyMinecart(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyEntity(entityTypes, v2) {

    constructor(v2: ink.ptms.adyeshach.core.entity.EntityInstance): this(EntityTypes.MINECART, v2)

    fun setCustomBlock(materialData: MaterialData) {
        v2 as AdyMinecart
        v2.setCustomBlock(materialData)
    }

    fun getCustomBlock(): MaterialData {
        v2 as AdyMinecart
        return v2.getCustomBlock()
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