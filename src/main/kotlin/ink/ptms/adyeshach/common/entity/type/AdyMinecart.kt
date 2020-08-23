package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
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

    init {
        registerMeta(at(11400 to 10, 11000 to 9, 10900 to 8), "customBlock", 0)
                .canEdit(false)
                .build()
        registerMeta(at(11400 to 11, 11000 to 10, 10900 to 9), "customBlockPosition", 6)
        registerMeta(at(11400 to 12, 11000 to 11, 10900 to 10), "showCustomBlock", false)
    }

    fun setCustomBlock(materialData: MaterialData) {
        customBlock = materialData.itemType
        customBlockData = materialData.data
        setMetadata("customBlock", NMS.INSTANCE.toBlockId(materialData))
    }

    fun getCustomBlock(): MaterialData {
        return MaterialData(customBlock, customBlockData)
    }
}