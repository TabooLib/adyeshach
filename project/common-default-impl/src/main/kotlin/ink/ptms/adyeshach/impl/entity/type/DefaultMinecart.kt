package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyMinecart
import org.bukkit.Material
import org.bukkit.material.MaterialData

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultMinecart
 *
 * @author 坏黑
 * @since 2022/6/29 19:06
 */
abstract class DefaultMinecart(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyMinecart {

    @Expose
    private var customBlock: Material = Material.AIR

    @Expose
    private var customBlockData: Byte = 0

    override fun setCustomBlock(materialData: MaterialData) {
        customBlock = materialData.itemType
        customBlockData = materialData.data
        setMetadata("customBlock", Adyeshach.api().getMinecraftAPI().getHelper().getBlockId(materialData))
    }

    override fun getCustomBlock(): MaterialData {
        return MaterialData(customBlock, customBlockData)
    }
}