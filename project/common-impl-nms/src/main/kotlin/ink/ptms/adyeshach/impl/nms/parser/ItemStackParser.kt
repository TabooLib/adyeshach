package ink.ptms.adyeshach.impl.nms.parser

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftMetadataParser
import ink.ptms.adyeshach.core.serializer.Serializer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.parser.ItemStackParser
 *
 * @author 坏黑
 * @since 2022/6/28 18:44
 */
class ItemStackParser : MinecraftMetadataParser<ItemStack>() {

    override fun parse(value: Any): ItemStack {
        return when (value) {
            is ItemStack -> value
            is String -> Serializer.toItemStack(value)
            else -> ItemStack(Material.BEDROCK)
        }
    }

    override fun createMeta(index: Int, value: ItemStack): MinecraftMeta {
        return metadataHandler().createItemStackMeta(index, value)
    }
}