package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.AdyeshachEntityMetadataRegistry
import ink.ptms.adyeshach.core.AdyeshachParallelTask
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.Meta
import ink.ptms.adyeshach.core.entity.type.AdyIronGolem
import ink.ptms.adyeshach.impl.description.DescEntityMeta
import ink.ptms.adyeshach.impl.description.DescEntityUnusedMeta
import ink.ptms.adyeshach.impl.entity.DefaultMetaMasked
import ink.ptms.adyeshach.impl.entity.DefaultMetaNatural
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.registerLifeCycleTask
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.unsafeLazy
import taboolib.platform.bukkit.parallel
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachEntityMetadataRegistry
 *
 * @author 坏黑
 * @since 2022/6/20 01:30
 */
class DefaultAdyeshachEntityMetadataRegistry : AdyeshachEntityMetadataRegistry {

    val descriptionMeta by unsafeLazy {
        DescEntityMeta(releaseResourceFile("core/description/entity_meta.desc", true).readBytes().inputStream())
    }

    val descriptionUnusedMeta by unsafeLazy {
        DescEntityUnusedMeta(releaseResourceFile("core/description/entity_meta_unused.desc", true).readBytes().inputStream())
    }

    init {
        parallel(AdyeshachParallelTask.DESCRIPTION_INIT) {
            descriptionMeta.init()
            descriptionUnusedMeta.init()
        }
    }

    override fun registerEntityMetaMask(type: Class<*>, index: Int, group: String, key: String, mask: Byte, def: Boolean) {
        registeredEntityMeta.computeIfAbsent(type) { ArrayList() } += DefaultMetaMasked<EntityInstance>(index, key, group, mask, def)
    }

    override fun registerEntityMetaNatural(type: Class<*>, index: Int, group: String, key: String, def: Any, parserId: String) {
        registeredEntityMeta.computeIfAbsent(type) { ArrayList() } += DefaultMetaNatural<Any, EntityInstance>(index, key, group, def, parserId)
    }

    override fun getEntityMeta(type: Class<*>): List<Meta<*>> {
        return registeredEntityMeta[type] ?: emptyList()
    }

    override fun getEntityUnusedMeta(type: Class<*>): List<String> {
        return descriptionUnusedMeta.metaMap[type] ?: emptyList()
    }

    override fun isUnusedMeta(entity: EntityInstance, id: String): Boolean {
        // 目前已知只有铁傀儡会因 Health 元数据而改变外貌
        if (id == "health" && entity !is AdyIronGolem) {
            return true
        }
        return descriptionUnusedMeta.metaMap.any { it.key.isInstance(entity) && it.value.contains(id) }
    }

    companion object {

        val registeredEntityMeta = LinkedHashMap<Class<*>, ArrayList<Meta<*>>>()
        val metaTypeLookup = ConcurrentHashMap<Class<*>, List<Meta<*>>>()
        val metaKeyLookup = ConcurrentHashMap<Class<*>, String>()

        @Awake(LifeCycle.CONST)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachEntityMetadataRegistry>(DefaultAdyeshachEntityMetadataRegistry())
        }
    }
}