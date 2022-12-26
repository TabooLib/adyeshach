package ink.ptms.adyeshach.core

import ink.ptms.adyeshach.core.entity.EntityInstance
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachEntitySerializer
 *
 * @author 坏黑
 * @since 2022/6/16 16:38
 */
interface AdyeshachEntitySerializer {

    /**
     * 从 Yaml 中读取单位
     */
    fun fromYaml(source: String, transfer: Function<String, String> = Function { it }): EntityInstance

    /**
     * 从 Json 中读取单位
     */
    fun fromJson(source: String, transfer: Function<String, String> = Function { it }): EntityInstance
}