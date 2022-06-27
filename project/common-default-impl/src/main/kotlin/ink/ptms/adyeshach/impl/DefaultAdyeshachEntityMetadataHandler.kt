package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.AdyeshachEntityMetadataHandler
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Meta
import ink.ptms.adyeshach.impl.description.DescEntityMeta
import ink.ptms.adyeshach.impl.entity.DefaultMetaMasked
import ink.ptms.adyeshach.impl.entity.DefaultMetaNatural
import taboolib.common.platform.function.releaseResourceFile

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachEntityMetadataHandler
 *
 * @author 坏黑
 * @since 2022/6/20 01:30
 */
class DefaultAdyeshachEntityMetadataHandler : AdyeshachEntityMetadataHandler {

    val description = DescEntityMeta(releaseResourceFile("description/entity_meta.desc", true).readBytes().inputStream())

    override fun registerEntityMetaMask(type: Class<*>, index: Int, key: String, mask: Byte, def: Boolean) {
        registeredEntityMeta.computeIfAbsent(type) { ArrayList() } += DefaultMetaMasked<EntityInstance>(index, key, mask, def)
    }

    override fun registerEntityMetaNatural(type: Class<*>, index: Int, key: String, def: Any) {
        registeredEntityMeta.computeIfAbsent(type) { ArrayList() } += DefaultMetaNatural<Any, EntityInstance>(index, key, def)
    }

    companion object {

        val registeredEntityMeta = LinkedHashMap<Class<*>, ArrayList<Meta<*>>>()
    }
}