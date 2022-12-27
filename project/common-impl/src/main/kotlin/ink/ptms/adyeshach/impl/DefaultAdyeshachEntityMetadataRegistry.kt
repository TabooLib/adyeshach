package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.AdyeshachEntityMetadataRegistry
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.Meta
import ink.ptms.adyeshach.impl.description.DescEntityMeta
import ink.ptms.adyeshach.impl.description.DescEntityUnusedMeta
import ink.ptms.adyeshach.impl.entity.DefaultMetaMasked
import ink.ptms.adyeshach.impl.entity.DefaultMetaNatural
import taboolib.common.LifeCycle
import taboolib.common.TabooLibCommon
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.releaseResourceFile
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachEntityMetadataRegistry
 *
 * @author 坏黑
 * @since 2022/6/20 01:30
 */
class DefaultAdyeshachEntityMetadataRegistry : AdyeshachEntityMetadataRegistry {

    val descriptionMeta = DescEntityMeta(releaseResourceFile("description/entity_meta.desc", true).readBytes().inputStream())
    val descriptionUnusedMeta = DescEntityUnusedMeta(releaseResourceFile("description/entity_meta_unused.desc", true).readBytes().inputStream())

    init {
        TabooLibCommon.postpone(LifeCycle.ENABLE) {
            descriptionMeta.init()
            descriptionUnusedMeta.init()
        }
    }

    override fun registerEntityMetaMask(type: Class<*>, index: Int, group: String, key: String, mask: Byte, def: Boolean) {
        registeredEntityMeta.computeIfAbsent(type) { ArrayList() } += DefaultMetaMasked<EntityInstance>(index, key, group, mask, def)
    }

    override fun registerEntityMetaNatural(type: Class<*>, index: Int, group: String, key: String, def: Any) {
        registeredEntityMeta.computeIfAbsent(type) { ArrayList() } += DefaultMetaNatural<Any, EntityInstance>(index, key, group, def)
    }

    override fun getEntityMeta(type: Class<*>): List<Meta<*>> {
        return registeredEntityMeta[type] ?: emptyList()
    }

    override fun getEntityUnusedMeta(type: Class<*>): List<String> {
        return descriptionUnusedMeta.metaMap[type] ?: emptyList()
    }

    companion object {

        val registeredEntityMeta = LinkedHashMap<Class<*>, ArrayList<Meta<*>>>()
        val metaTypeLookup = ConcurrentHashMap<Class<*>, List<Meta<*>>>()
        val metaKeyLookup = ConcurrentHashMap<Class<*>, String>()

        @Awake(LifeCycle.LOAD)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachEntityMetadataRegistry>(DefaultAdyeshachEntityMetadataRegistry())
        }
    }
}