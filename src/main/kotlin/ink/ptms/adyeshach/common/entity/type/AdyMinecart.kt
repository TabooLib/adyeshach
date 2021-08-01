package ink.ptms.adyeshach.common.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.getName
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

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
        registerMeta(at(11700 to 11, 11400 to 10, 11000 to 9, 10900 to 8), "customBlock", 0)
                .canEdit(false)
                .build()
        registerMeta(at(11700 to 12, 11400 to 11, 11000 to 10, 10900 to 9), "customBlockPosition", 6)
        registerMeta(at(11700 to 13, 11400 to 12, 11000 to 11, 10900 to 10), "showCustomBlock", false)
        registerEditor("block")
                .reset { _, _ ->
                    setCustomBlock(MaterialData(Material.AIR, 0))
                }
                .modify { player, entity, _ ->
                    player.openMenu<Basic>("Adyeshach Editor : Input") {
                        rows(1)
                        map("####@####")
                        set('#', XMaterial.BLACK_STAINED_GLASS_PANE) {
                            name = "§f"
                        }
                        set('@', getCustomBlock().toItemStack(1))
                        onClick('#')
                        onClose {
                            try {
                                setCustomBlock((it.inventory.getItem(4) ?: ItemStack(Material.AIR)).data!!)
                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                            Editor.open(player, entity)
                        }
                    }
                }
                .display { player, _, _ ->
                    getCustomBlock().toItemStack(1).getName(player)
                }
    }

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