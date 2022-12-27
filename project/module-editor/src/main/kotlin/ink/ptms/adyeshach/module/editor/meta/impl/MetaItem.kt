package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.type.AdyFallingBlock
import ink.ptms.adyeshach.core.entity.type.AdyMinecart
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
    
    open fun getMetaItem(entity: EntityInstance): ItemStack {
        return entity.getMetadata(key)
    }
    
    open fun setMetaItem(entity: EntityInstance, itemStack: ItemStack) {
        entity.setMetadata(key, itemStack)
    }

    override fun open(entity: EntityInstance, player: Player, def: String) {
        player.openMenu<Basic>(player.lang("input-item")) {
            handLocked(false)
            rows(1)
            map("####@####")
            set('#', XMaterial.BLACK_STAINED_GLASS_PANE) { name = "§f" }
            set('@', getMetaItem(entity))
            onClick('#')
            onClose {
                // 修改元数据
                setMetaItem(entity, it.inventory.getItem(4) ?: ItemStack(Material.AIR))
                // 刷新页面
                ChatEditor.refresh(player)
            }
        }
    }

    class Mat(key: String) : MetaItem(key) {

        override fun getMetaItem(entity: EntityInstance): ItemStack {
            return entity.getMetadata<MaterialData>(key).toItemStack(1)
        }

        override fun setMetaItem(entity: EntityInstance, itemStack: ItemStack) {
            entity.setMetadata(key, itemStack.data ?: MaterialData(itemStack.type, itemStack.durability.toByte()))
        }
    }

    class Minecart : MetaItem("null") {

        override fun getMetaItem(entity: EntityInstance): ItemStack {
            entity as AdyMinecart
            return entity.getCustomBlock().toItemStack(1)
        }

        override fun setMetaItem(entity: EntityInstance, itemStack: ItemStack) {
            entity as AdyMinecart
            entity.setCustomBlock(itemStack.data ?: MaterialData(itemStack.type, itemStack.durability.toByte()))
        }
    }

    class FallingBlock : MetaItem("null") {

        override fun getMetaItem(entity: EntityInstance): ItemStack {
            entity as AdyFallingBlock
            return MaterialData(entity.getMaterial(), entity.getData()).toItemStack(1)
        }

        override fun setMetaItem(entity: EntityInstance, itemStack: ItemStack) {
            entity as AdyFallingBlock
            entity.setMaterial(itemStack.type, itemStack.durability.toByte())
        }
    }
}