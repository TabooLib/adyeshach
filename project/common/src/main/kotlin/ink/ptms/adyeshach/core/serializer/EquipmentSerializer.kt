package ink.ptms.adyeshach.core.serializer

import org.bukkit.inventory.ItemStack

/**
 * 装备序列化器接口
 * 
 * 允许开发者自定义实体装备（ItemStack）的序列化和反序列化策略。
 * 
 * ## 使用场景
 * - 需要将装备存储到对 Base64 不友好的系统
 * - 需要人类可读的装备配置格式（如 JSON、YAML）
 * - 需要压缩或自定义编码以优化存储空间
 * - 需要与第三方系统交换装备数据
 * 
 * ## 使用示例
 * ```kotlin
 * // 实现自定义序列化器
 * object JsonEquipmentSerializer : EquipmentSerializer {
 *     override fun serialize(itemStack: ItemStack): String {
 *         // 将 ItemStack 转换为 JSON 字符串
 *         return customJsonEncoder.encode(itemStack)
 *     }
 *     
 *     override fun deserialize(data: String): ItemStack {
 *         // 从 JSON 字符串恢复 ItemStack
 *         return customJsonEncoder.decode(data)
 *     }
 * }
 * 
 * // 在插件启动时注册
 * Serializer.registerEquipmentSerializer(JsonEquipmentSerializer)
 * ```
 * 
 * @author sky
 * @since 2025
 */
interface EquipmentSerializer {

    /**
     * 将 ItemStack 序列化为字符串
     * 
     * @param itemStack 要序列化的物品
     * @return 序列化后的字符串表示
     * @throws Exception 序列化失败时抛出异常（将被包装为 EquipmentSerializationException）
     */
    fun serialize(itemStack: ItemStack): String

    /**
     * 将字符串反序列化为 ItemStack
     * 
     * @param data 序列化的字符串数据
     * @return 反序列化后的 ItemStack 对象
     * @throws Exception 反序列化失败时抛出异常（将被包装为 EquipmentSerializationException）
     */
    fun deserialize(data: String): ItemStack
}
