package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.*
import ink.ptms.adyeshach.impl.util.ifTrue
import org.bukkit.Material
import org.bukkit.material.MaterialData
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.parseToXMaterial

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultMinecart
 *
 * @author 坏黑
 * @since 2022/6/29 19:06
 */
@Suppress("LeakingThis")
abstract class DefaultMinecart(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyMinecart {

    @Expose
    private var customBlock: Material = when (this) {
        is AdyMinecartChest -> Material.CHEST
        is AdyMinecartFurnace -> Material.FURNACE
        is AdyMinecartHopper -> Material.HOPPER
        is AdyMinecartSpawner -> XMaterial.SPAWNER.parseMaterial()!!
        is AdyMinecartCommandBlock -> XMaterial.COMMAND_BLOCK.parseMaterial()!!
        is AdyMinecartTNT -> Material.TNT
        else -> Material.AIR
    }

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

    @Suppress("SpellCheckingInspection")
    override fun setCustomMeta(key: String, value: String): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "customblock", "custom_block" -> {
                val mat = value.parseToXMaterial()
                setCustomBlock(MaterialData(mat.parseMaterial() ?: Material.STONE, mat.data))
                true
            }
            else -> false
        }
    }
}