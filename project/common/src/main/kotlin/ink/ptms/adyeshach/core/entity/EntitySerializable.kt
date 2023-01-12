package ink.ptms.adyeshach.core.entity

import taboolib.library.configuration.ConfigurationSection

import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.EntitySerializable
 *
 * @author 坏黑
 * @since 2022/6/19 16:07
 */
interface EntitySerializable {

    /**
     * 转换为 Json 字符串
     */
    fun toJson(): String

    /**
     * 转换为 Yaml 对象
     *
     * @param transfer 节点名称转换函数
     */
    fun toYaml(transfer: Function<String, String> = Function { it }): ConfigurationSection

    /**
     * 转换为 Yaml 并写入 ConfigurationSection 对象
     *
     * @param transfer 节点名称转换函数
     */
    fun toSection(section: ConfigurationSection, transfer: Function<String, String> = Function { it })
}