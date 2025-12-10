package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import taboolib.library.configuration.ConfigurationSection
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.SerializationHandler
 *
 * 负责实体的序列化
 */
class SerializationHandler(private val self: DefaultEntityInstance) {

    /**
     * 转换为 Json 字符串
     */
    fun toJson(): String {
        return Adyeshach.api().getEntitySerializer().toJson(self)
    }

    /**
     * 转换为 Yaml 对象
     *
     * @param transfer 节点名称转换函数
     */
    fun toYaml(transfer: Function<String, String> = Function { it }): ConfigurationSection {
        return Adyeshach.api().getEntitySerializer().toYaml(self, transfer)
    }

    /**
     * 转换为 Yaml 并写入 ConfigurationSection 对象
     *
     * @param transfer 节点名称转换函数
     */
    fun toSection(section: ConfigurationSection, transfer: Function<String, String> = Function { it }) {
        Adyeshach.api().getEntitySerializer().toSection(self, section, transfer)
    }
}
