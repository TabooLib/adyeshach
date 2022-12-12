package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.type.AdyEntity

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class PrepareMetaNatural(name: String, val value: Any) : PrepareMeta(name) {

    override fun register(entityClass: Class<out AdyEntity>, index: Int) {
        Adyeshach.api().getEntityMetadataHandler().registerEntityMetaNatural(entityClass, index, name, value)
    }

    override fun toString(): String {
        return "$name($value)"
    }
}