package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.AdyeshachEntityTypeHandler
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntitySize
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.type.AdyEntity
import ink.ptms.adyeshach.impl.description.DescEntityTypes
import ink.ptms.adyeshach.impl.description.Entity
import org.bukkit.entity.EntityType
import taboolib.common.platform.function.releaseResourceFile

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.DefaultAdyeshachEntityTypeHandler
 *
 * @author 坏黑
 * @since 2022/6/19 15:56
 */
class DefaultAdyeshachEntityTypeHandler : AdyeshachEntityTypeHandler {

    val types = HashMap<EntityTypes, Entity>()
    val description = DescEntityTypes(releaseResourceFile("description/entity_types.desc", true).readBytes().inputStream())

    init {
        description.types.forEach { types[it.adyeshachType] = it }
    }

    override fun getBukkitEntityType(entityType: EntityTypes): EntityType {
        return types[entityType]!!.bukkitType ?: error("${entityType.name} is not supported")
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
        TODO("Not yet implemented")
    }

    override fun getEntityTypeFromAdyClass(clazz: Class<out AdyEntity>): EntityTypes? {
        TODO("Not yet implemented")
    }
}