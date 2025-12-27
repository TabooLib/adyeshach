package ink.ptms.adyeshach.core.serializer

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Base64 装备序列化器
 * 
 * 使用 Base64 编码对 ItemStack 进行序列化和反序列化。
 * 这是 Adyeshach 的默认序列化策略，与现有行为完全兼容。
 * 
 * ## 实现原理
 * 1. 序列化：ItemStack -> BukkitObjectOutputStream -> ByteArray -> Base64 String
 * 2. 反序列化：Base64 String -> ByteArray -> BukkitObjectInputStream -> ItemStack
 * 
 * @author sky
 * @since 2025
 */
open class Base64EquipmentSerializer : EquipmentSerializer {

    /**
     * 将 ItemStack 序列化为 Base64 编码字符串
     * 
     * @param itemStack 要序列化的物品
     * @return Base64 编码的字符串
     * @throws EquipmentSerializationException 序列化失败时抛出
     */
    override fun serialize(itemStack: ItemStack): String {
        try {
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                BukkitObjectOutputStream(byteArrayOutputStream).use { bukkitObjectOutputStream ->
                    bukkitObjectOutputStream.writeObject(itemStack)
                    return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
                }
            }
        } catch (e: Exception) {
            throw EquipmentSerializationException(
                "Failed to serialize ItemStack: ${itemStack.type}",
                serializer = "Base64EquipmentSerializer",
                cause = e
            )
        }
    }

    /**
     * 将 Base64 编码字符串反序列化为 ItemStack
     * 
     * @param data Base64 编码的字符串
     * @return 反序列化后的 ItemStack 对象
     * @throws EquipmentSerializationException 反序列化失败时抛出
     */
    override fun deserialize(data: String): ItemStack {
        try {
            ByteArrayInputStream(Base64.getDecoder().decode(data)).use { byteArrayInputStream ->
                BukkitObjectInputStream(byteArrayInputStream).use { bukkitObjectInputStream ->
                    return bukkitObjectInputStream.readObject() as ItemStack
                }
            }
        } catch (e: Exception) {
            val preview = if (data.length > 50) "${data.take(50)}..." else data
            throw EquipmentSerializationException(
                "Failed to deserialize ItemStack from data: $preview",
                serializer = "Base64EquipmentSerializer",
                cause = e
            )
        }
    }
}
