package ink.ptms.adyeshach.common.api

import ink.ptms.adyeshach.common.entity.type.AdyEntity

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.AdyeshachEntityMetadataHandler
 *
 * @author 坏黑
 * @since 2022/6/16 16:40
 */
interface AdyeshachEntityMetadataHandler {

    /**
     * 注册元数据模型（布尔值）
     */
    fun registerEntityMetaMask(type: Class<out AdyEntity>, index: Int, key: String, mask: Byte, def: Boolean = false)

    /**
     * 注册元数据模型（专业类型）
     */
    fun registerEntityMetaNatural(type: Class<out AdyEntity>, index: Int, key: String, def: Any)

    /**
     * 获取实体无意义的元数据
     */
    fun getEntityUnusedMeta(type: Class<out AdyEntity>): List<String>
}