package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.common.api.Adyeshach

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class PrepareMetaNatural(name: String, val value: Any) : PrepareMeta(name) {

    override fun register(entityClass: Class<*>, index: Int) {
        Adyeshach.api().getEntityMetadataHandler().registerEntityMetaNatural(entityClass, index, name, value)
    }

    override fun toString(): String {
        return "$name($value)"
    }
}