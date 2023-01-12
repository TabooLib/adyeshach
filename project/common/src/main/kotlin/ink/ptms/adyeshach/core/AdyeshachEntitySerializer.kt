package ink.ptms.adyeshach.core

import ink.ptms.adyeshach.core.entity.EntityInstance
import taboolib.library.configuration.ConfigurationSection
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachEntitySerializer
 *
 * @author 坏黑
 * @since 2022/6/16 16:38
 */
interface AdyeshachEntitySerializer {

    /** 写为 Json */
    fun toJson(entity: EntityInstance): String

    /** 写为 Yaml */
    fun toYaml(entity: EntityInstance, transfer: Function<String, String>): ConfigurationSection

    /** 写入 ConfigurationSection */
    fun toSection(entity: EntityInstance, section: ConfigurationSection, transfer: Function<String, String>)

    /** 从 Yaml 中读取单位 */
    fun fromYaml(source: String, transfer: Function<String, String> = Function { it }): EntityInstance

    /** 从 Json 中读取单位 */
    fun fromJson(source: String, transfer: Function<String, String> = Function { it }): EntityInstance

    /** 从 ConfigurationSection 中读取单位 */
    fun fromSection(section: ConfigurationSection, transfer: Function<String, String> = Function { it }): EntityInstance
}