package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.common.entity.type.AdyEntity

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
abstract class PrepareMeta(val name: String) {

    abstract fun register(entityClass: Class<out AdyEntity>, index: Int)

    override fun toString(): String {
        return name
    }
}