package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import io.izzel.taboolib.module.i18n.I18n
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import io.izzel.taboolib.util.lite.Materials
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
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
        registerEditor("block")
                .reset { entity, meta ->
                    setCustomBlock(MaterialData(Material.AIR, 0))
                }
                .modify { player, entity, meta ->
                    MenuBuilder.builder(Adyeshach.plugin)
                            .title("Adyeshach Editor : Input")
                            .rows(1)
                            .items("#####@#####")
                            .put('#', ItemBuilder(Materials.BLACK_STAINED_GLASS_PANE.parseItem()).name("§f").build())
                            .put('@', getCustomBlock().toItemStack(1))
                            .event {
                                if (it.slot == '#') {
                                    it.isCancelled = true
                                }
                            }.close {
                                try {
                                    setCustomBlock((it.inventory.getItem(4) ?: ItemStack(Material.AIR)).data!!)
                                } catch (t: Throwable) {
                                    t.printStackTrace()
                                }
                                Editor.open(player, entity)
                            }.open(player)
                }
                .display { player, entity, meta ->
                    I18n.get().getName(player, getCustomBlock().toItemStack(1))
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
}