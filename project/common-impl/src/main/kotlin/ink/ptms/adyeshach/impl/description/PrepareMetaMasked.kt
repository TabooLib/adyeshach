package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.type.AdyEntity

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class PrepareMetaMasked(name: String, val mask: Byte, val value: Boolean) : PrepareMeta(name) {

    override fun register(entityClass: Class<*>, index: Int, group: String) {
        Adyeshach.api().getEntityMetadataRegistry().registerEntityMetaMask(entityClass, index, group, name, mask, value)
    }

    override fun toString(): String {
        return "$name($mask, $value)"
    }
}