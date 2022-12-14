package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachEntityTypeHandler
import ink.ptms.adyeshach.core.entity.EntityBase
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntitySize
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.path.PathType
import ink.ptms.adyeshach.core.entity.type.AdyEntity
import ink.ptms.adyeshach.core.entity.type.errorBy
import ink.ptms.adyeshach.impl.bytecode.SimpleEntityGenerator
import ink.ptms.adyeshach.impl.description.DescEntityTypes
import ink.ptms.adyeshach.impl.description.Entity
import ink.ptms.adyeshach.impl.entity.DefaultEditable
import org.bukkit.entity.EntityType
import taboolib.common.LifeCycle
import taboolib.common.TabooLibCommon
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.nms.AsmClassLoader

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachEntityTypeHandler
 *
 * @author 坏黑
 * @since 2022/6/19 15:56
 */
class DefaultAdyeshachEntityTypeHandler : AdyeshachEntityTypeHandler {

    val callback = ArrayList<AdyeshachEntityTypeHandler.GenerateCallback>()

    val types = HashMap<EntityTypes, Entity>()
        get() {
            if (field.isEmpty()) {
                description.init()
                description.types.forEach { field[it.adyeshachType] = it }
            }
            return field
        }

    val entityClass = HashMap<EntityTypes, EntityBase>()
        get() {
            if (field.isEmpty()) {
                val time = System.currentTimeMillis()
                field.putAll(generateEntityBase())
                info("Proxy classes has been generated (${System.currentTimeMillis() - time}ms)")
            }
            return field
        }

    val description = DescEntityTypes(releaseResourceFile("description/entity_types.desc", true).readBytes().inputStream())
    val generator = SimpleEntityGenerator()

    init {
        TabooLibCommon.postpone(LifeCycle.ENABLE) { entityClass }
        // 注册生成回调
        prepareGenerate(object : AdyeshachEntityTypeHandler.GenerateCallback {

            override fun invoke(entityType: EntityTypes, interfaces: List<String>): List<String> {
                val array = ArrayList<String>()
                // 是否加载编辑器模块
                if (Adyeshach.editor() != null) {
                    array += DefaultEditable::class.java.name
                }
                return array
            }
        })
    }

    override fun getBukkitEntityType(entityType: EntityTypes): EntityType {
        return types[entityType]!!.bukkitType ?: errorBy("error-entity-type-not-supported", entityType.name)
    }

    override fun getBukkitEntityId(entityType: EntityTypes): Int {
        return types[entityType]!!.id
    }

    override fun getBukkitEntityAliases(entityType: EntityTypes): List<String> {
        return types[entityType]!!.aliases
    }

    override fun getEntitySize(entityType: EntityTypes): EntitySize {
        return types[entityType]!!.size
    }

    override fun getEntityPathType(entityType: EntityTypes): PathType {
        return types[entityType]!!.path
    }

    override fun getEntityInstance(entityType: EntityTypes): EntityInstance {
        return entityClass[entityType]!!.createEmpty() as EntityInstance
    }

    override fun getEntityFlags(entityType: EntityTypes): List<String> {
        return types[entityType]!!.flags
    }

    override fun getEntityClientUpdateInterval(entityType: EntityTypes): Int {
        return types[entityType]!!.updateInterval
    }

    override fun getEntityTypeFromAdyClass(clazz: Class<out AdyEntity>): EntityTypes? {
        return types.values.firstOrNull { it.adyeshachInterface == clazz }?.adyeshachType
    }

    override fun prepareGenerate(callback: AdyeshachEntityTypeHandler.GenerateCallback) {
        this.callback += callback
    }

    fun generateEntityBase(): Map<EntityTypes, EntityBase> {
        val map = HashMap<EntityTypes, EntityBase>()
        types.forEach { (k, v) ->
            val name = "adyeshach.Proxy${v.adyeshachInterface.simpleName}"
            val interfaces = if (v.instanceWithInterface) arrayListOf(v.namespace) else arrayListOf()
            // 执行回调函数
            callback.forEach { interfaces += it(k, interfaces) }
            // 生成类
            val newClass = AsmClassLoader.createNewClass(name, generator.generate(name, v.instance.replace('.', '/'), interfaces.map { it.replace('.', '/') }))
            // 生成实例
            map[k] = newClass.invokeConstructor(k) as EntityBase
        }
        return map
    }
}