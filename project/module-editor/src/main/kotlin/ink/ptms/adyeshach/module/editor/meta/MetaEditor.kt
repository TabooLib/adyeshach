package ink.ptms.adyeshach.module.editor.meta

import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.Meta
import ink.ptms.adyeshach.module.editor.EditType
import ink.ptms.adyeshach.module.editor.meta.impl.*
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.MetaEditor
 *
 * @author 坏黑
 * @since 2022/12/27 03:49
 */
interface MetaEditor {

    fun open(entity: EntityInstance, player: Player)

    companion object {

        /**
         * 获取固定类型的编辑器
         */
        fun getMetaEditor(type: EditType, key: String): MetaEditor {
            return when (type) {
                EditType.SIGN -> MetaPrimitive(key)
                EditType.EQUIPMENT -> MetaEquipment()
                else -> error("Unsupported type: $type")
            }
        }

        /**
         * 通过元数据获取编辑器
         */
        fun getMetaEditor(meta: Meta<*>): MetaEditor? {
            return when (meta.def) {
                // 基础类型
                is Int, is Byte, is Float, is String, is TextComponent -> MetaPrimitive(meta.key)
                // 材质相关
                is ItemStack -> MetaItem(meta.key)
                is MaterialData -> MetaItem.Mat(meta.key)
                // 位置相关
                is Vector -> TODO()
                is EulerAngle -> TODO()
                // 枚举相关
                is VillagerData -> MetaVillager()
                is Enum<*> -> MetaEnum(meta.key, meta.def::class.java)
                // 不支持
                else -> null
            }
        }

        /**
         * 获取自定义元数据编辑器
         */
        fun getCustomMetaEditor(entity: EntityInstance, key: String): MetaEditor? {
            TODO()
        }
    }
}