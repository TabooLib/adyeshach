package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.ChatEditor
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaItem
 *
 * @author 坏黑
 * @since 2022/12/27 04:04
 */
open class MetaItem(val key: String) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        player.openMenu<Basic>(player.lang("input-item")) {
            handLocked(false)
            rows(1)
            map("####@####")
            set('#', XMaterial.BLACK_STAINED_GLASS_PANE) { name = "§f" }
            set('@', entity.getMetadata<ItemStack>(key))
            onClick('#')
            onClose {
                // 修改元数据
                entity.setMetadata(key, it.inventory.getItem(4) ?: ItemStack(Material.AIR))
                // 刷新页面
                ChatEditor.refresh(player)
            }
        }
    }

    class Mat(key: String) : MetaItem(key) {

        override fun open(entity: EntityInstance, player: Player, def: String) {
            player.openMenu<Basic>(player.lang("input-item")) {
                handLocked(false)
                rows(1)
                map("####@####")
                set('#', XMaterial.BLACK_STAINED_GLASS_PANE) { name = "§f" }
                set('@', entity.getMetadata<MaterialData>(key).toItemStack(1))
                onClick('#')
                onClose {
                    // 修改元数据
                    val itemStack = it.inventory.getItem(4) ?: ItemStack(Material.AIR)
                    entity.setMetadata(key, itemStack.data ?: MaterialData(itemStack.type, itemStack.durability.toByte()))
                    // 刷新页面
                    ChatEditor.refresh(player)
                }
            }
        }
    }
}