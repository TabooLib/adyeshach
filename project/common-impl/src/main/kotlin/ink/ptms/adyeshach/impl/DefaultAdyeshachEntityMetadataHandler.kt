package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.AdyeshachEntityMetadataHandler
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Meta
import ink.ptms.adyeshach.common.entity.type.AdyEntity
import ink.ptms.adyeshach.impl.description.DescEntityMeta
import ink.ptms.adyeshach.impl.description.DescEntityUnusedMeta
import ink.ptms.adyeshach.impl.entity.DefaultMetaMasked
import ink.ptms.adyeshach.impl.entity.DefaultMetaNatural
import taboolib.common.LifeCycle
import taboolib.common.TabooLibCommon
import taboolib.common.platform.function.releaseResourceFile
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachEntityMetadataHandler
 *
 * @author 坏黑
 * @since 2022/6/20 01:30
 */
class DefaultAdyeshachEntityMetadataHandler : AdyeshachEntityMetadataHandler {

    val descriptionMeta = DescEntityMeta(releaseResourceFile("description/entity_meta.desc", true).readBytes().inputStream())
    val descriptionUnusedMeta = DescEntityUnusedMeta(releaseResourceFile("description/entity_meta_unused.desc", true).readBytes().inputStream())

    init {
        TabooLibCommon.postpone(LifeCycle.ENABLE) {
            descriptionMeta.init()
            descriptionUnusedMeta.init()
        }
    }

    override fun registerEntityMetaMask(type: Class<out AdyEntity>, index: Int, key: String, mask: Byte, def: Boolean) {
        registeredEntityMeta.computeIfAbsent(type) { ArrayList() } += DefaultMetaMasked<EntityInstance>(index, key, mask, def)
    }

    override fun registerEntityMetaNatural(type: Class<out AdyEntity>, index: Int, key: String, def: Any) {
        registeredEntityMeta.computeIfAbsent(type) { ArrayList() } += DefaultMetaNatural<Any, EntityInstance>(index, key, def)
    }

    override fun getEntityUnusedMeta(type: Class<out AdyEntity>): List<String> {
        return descriptionUnusedMeta.metaMap[type] ?: emptyList()
    }

    companion object {

        val registeredEntityMeta = LinkedHashMap<Class<*>, ArrayList<Meta<*>>>()
        val metaTypeLookup = ConcurrentHashMap<Class<*>, List<Meta<*>>>()
        val metaKeyLookup = ConcurrentHashMap<Class<*>, String>()
    }
}