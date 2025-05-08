package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachEntityTypeRegistry
import ink.ptms.adyeshach.core.AdyeshachParallelTask
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
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.execution
import taboolib.common.util.t
import taboolib.common.util.unsafeLazy
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.nms.AsmClassLoader
import taboolib.platform.bukkit.parallel

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachEntityTypeRegistry
 *
 * @author 坏黑
 * @since 2022/6/19 15:56
 */
class DefaultAdyeshachEntityTypeRegistry : AdyeshachEntityTypeRegistry {

    /** 实体类生成器 */
    val generator = SimpleEntityGenerator()

    /** 实体类生成回调 */
    val callback = ArrayList<AdyeshachEntityTypeRegistry.GenerateCallback>()

    /** 实体类型描述文件 */
    val description by unsafeLazy {
        DescEntityTypes(releaseResourceFile("core/description/entity_types.desc", true).readBytes().inputStream())
    }

    /** 描述文件中的实体数据 */
    val descriptionEntityMap = HashMap<EntityTypes, Entity>()
        get() {
            if (field.isEmpty()) {
                description.init()
                description.types.forEach { field[it.adyeshachType] = it }
            }
            return field
        }

    /** 所有实体对象的原件，用于克隆实体 */
    val originEntityBaseMap = HashMap<EntityTypes, EntityBase>()

    init {
        // 生成实体类
        parallel(AdyeshachParallelTask.GENERATE_ENTITY_CLASS, dependOn = AdyeshachParallelTask.DESCRIPTION_INIT) {
            val (_, cost) = execution { originEntityBaseMap += generateEntityBase() }
            info(
                """
                    代理类已生成，用时 $cost 毫秒。
                    Proxy classes generated in $cost ms.
                """.t()
            )
        }
        // 注册生成回调
        prepareGenerate(object : AdyeshachEntityTypeRegistry.GenerateCallback {

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
        return descriptionEntityMap[entityType]!!.bukkitType ?: errorBy("error-entity-type-not-supported", entityType.name)
    }

    override fun getBukkitEntityTypeOrNull(entityType: EntityTypes): EntityType? {
        return descriptionEntityMap[entityType]!!.bukkitType
    }

    override fun getBukkitEntityId(entityType: EntityTypes): Int {
        return descriptionEntityMap[entityType]!!.id
    }

    override fun getBukkitEntityAliases(entityType: EntityTypes): List<String> {
        return descriptionEntityMap[entityType]!!.aliases
    }

    override fun getEntitySize(entityType: EntityTypes): EntitySize {
        return descriptionEntityMap[entityType]!!.size
    }

    override fun getEntityPathType(entityType: EntityTypes): PathType {
        return descriptionEntityMap[entityType]!!.path
    }

    override fun getEntityInstance(entityType: EntityTypes): EntityInstance {
        return originEntityBaseMap[entityType]!!.createEmpty() as EntityInstance
    }

    override fun getEntityClass(entityType: EntityTypes): Class<EntityBase> {
        return originEntityBaseMap[entityType]!!.javaClass
    }

    override fun getEntityFlags(entityType: EntityTypes): List<String> {
        return descriptionEntityMap[entityType]!!.flags
    }

    override fun getEntityClientUpdateInterval(entityType: EntityTypes): Int {
        return descriptionEntityMap[entityType]!!.updateInterval
    }

    override fun getEntityTypeFromAdyClass(clazz: Class<*>): EntityTypes? {
        return descriptionEntityMap.values.firstOrNull { it.adyeshachInterface == clazz }?.adyeshachType
    }

    override fun getEntityTypeFromBukkit(entityType: EntityType): EntityTypes {
        return descriptionEntityMap.values.firstOrNull { it.bukkitType == entityType }?.adyeshachType ?: errorBy("error-entity-type-not-supported", entityType.name)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getAdyClassFromEntityType(entityType: EntityTypes): Class<out AdyEntity> {
        return descriptionEntityMap.values.first { it.adyeshachType == entityType }.adyeshachInterface as Class<out AdyEntity>
    }

    override fun prepareGenerate(callback: AdyeshachEntityTypeRegistry.GenerateCallback) {
        this.callback += callback
    }

    fun generateEntityBase(): Map<EntityTypes, EntityBase> {
        val map = HashMap<EntityTypes, EntityBase>()
        descriptionEntityMap.forEach { (k, v) ->
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

    companion object {

        @Awake(LifeCycle.CONST)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachEntityTypeRegistry>(DefaultAdyeshachEntityTypeRegistry())
        }
    }
}