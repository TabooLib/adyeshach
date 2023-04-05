package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.type.AdyEntity

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class PrepareMetaNatural(name: String, val value: Any, val parserId: String) : PrepareMeta(name) {

    override fun register(entityClass: Class<*>, index: Int, group: String) {
        Adyeshach.api().getEntityMetadataRegistry().registerEntityMetaNatural(entityClass, index, group, name, value, parserId)
    }

    override fun toString(): String {
        return "$name($value)"
    }
}