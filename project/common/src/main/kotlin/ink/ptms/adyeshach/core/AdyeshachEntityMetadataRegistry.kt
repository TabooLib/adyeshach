package ink.ptms.adyeshach.core

import ink.ptms.adyeshach.core.entity.Meta

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachEntityMetadataHandler
 *
 * @author 坏黑
 * @since 2022/6/16 16:40
 */
interface AdyeshachEntityMetadataRegistry {

    /**
     * 注册元数据模型（布尔值）
     */
    fun registerEntityMetaMask(type: Class<*>, index: Int, group: String, key: String, mask: Byte, def: Boolean = false)

    /**
     * 注册元数据模型（专业类型）
     */
    fun registerEntityMetaNatural(type: Class<*>, index: Int, group: String, key: String, def: Any)

    /**
     * 获取实体的元数据
     */
    fun getEntityMeta(type: Class<*>): List<Meta<*>>

    /**
     * 获取实体无意义的元数据
     */
    fun getEntityUnusedMeta(type: Class<*>): List<String>
}