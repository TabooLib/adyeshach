package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.type.AdyEntity

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class PrepareMetaNatural(name: String, val value: Any) : PrepareMeta(name) {

    override fun register(entityClass: Class<out AdyEntity>, index: Int, group: String) {
        Adyeshach.api().getEntityMetadataHandler().registerEntityMetaNatural(entityClass, index, group, name, value)
    }

    override fun toString(): String {
        return "$name($value)"
    }
}