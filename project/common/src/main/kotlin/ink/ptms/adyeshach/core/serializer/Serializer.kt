package ink.ptms.adyeshach.core.serializer

import com.google.gson.GsonBuilder
import org.bukkit.inventory.ItemStack
import taboolib.common.io.runningClassMapInJar

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.serializer.Serializer
 *
 * @author sky
 * @since 2021/5/29 12:27 上午
 */
object Serializer {

    val gson = GsonBuilder()
        .setPrettyPrinting()
        .excludeFieldsWithoutExposeAnnotation().run {
            runningClassMapInJar.forEach { (_, cls) ->
                if (cls.hasAnnotation(SerializerType::class.java)) {
                    val type = cls.getAnnotation(SerializerType::class.java).type("baseClass").instance
                    registerTypeHierarchyAdapter(type, cls.newInstance())
                }
            }
            create()
        }!!

    /**
     * 当前活动的装备序列化器
     * 
     * 使用 @Volatile 保证多线程环境下的可见性。
     * 默认使用 Base64EquipmentSerializer 以保持向后兼容。
     */
    @Volatile
    private var equipmentSerializer: EquipmentSerializer = Base64EquipmentSerializer()

    /**
     * 注册自定义装备序列化器
     * 
     * 该方法允许开发者替换默认的 Base64 序列化策略。
     * 建议在插件初始化阶段调用，避免运行时切换导致数据不一致。
     * 
     * @param serializer 自定义的序列化器实现
     */
    fun registerEquipmentSerializer(serializer: EquipmentSerializer) {
        equipmentSerializer = serializer
    }

    /**
     * 获取当前活动的装备序列化器
     * 
     * @return 当前使用的序列化器实例
     */
    fun getEquipmentSerializer(): EquipmentSerializer {
        return equipmentSerializer
    }

    /**
     * 将 ItemStack 序列化为字符串
     * 
     * 该方法委托给当前活动的装备序列化器。
     * 保留此方法以维持向后兼容性。
     * 
     * @param itemStack 要序列化的物品
     * @return 序列化后的字符串
     */
    fun fromItemStack(itemStack: ItemStack): String {
        return equipmentSerializer.serialize(itemStack)
    }

    /**
     * 将字符串反序列化为 ItemStack
     * 
     * 该方法委托给当前活动的装备序列化器。
     * 保留此方法以维持向后兼容性。
     * 
     * @param data 序列化的字符串数据
     * @return 反序列化后的 ItemStack 对象
     */
    fun toItemStack(data: String): ItemStack {
        return equipmentSerializer.deserialize(data)
    }
}