package ink.ptms.adyeshach.common.api

import ink.ptms.adyeshach.common.entity.EntityInstance
import taboolib.library.configuration.ConfigurationSection
import java.io.File
import java.io.InputStream
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.AdyeshachEntitySerializer
 *
 * @author 坏黑
 * @since 2022/6/16 16:38
 */
interface AdyeshachEntitySerializer {

    /**
     * 从配置文件读取单位
     *
     * @param section 原始 yaml 实例
     */
    fun fromYaml(section: ConfigurationSection, transfer: Function<String, String> = Function { it }): EntityInstance?

    /**
     * 从配置文件读取单位
     *
     * @param source 序列化后的 yaml 文件
     */
    fun fromYaml(source: String): EntityInstance?

    /**
     * 从 Json 输入流中读取单位
     */
    fun fromJson(inputStream: InputStream): EntityInstance?

    /**
     * 从 Json 文件中读取单位
     */
    fun fromJson(file: File): EntityInstance?

    /**
     * 从 Json 序列化后文件中读取单位
     */
    fun fromJson(source: String): EntityInstance?
}