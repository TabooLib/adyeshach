package ink.ptms.adyeshach.common.api

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyEntity

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.AdyeshachEntityTypeHandler
 *
 * @author 坏黑
 * @since 2022/6/16 18:31
 */
interface AdyeshachEntityTypeHandler {

    /**
     * 通过与 AdyEntity 类似的接口类获取对应实体类型
     */
    fun getEntityTypeFromAdyClass(clazz: Class<out AdyEntity>): EntityTypes?
}