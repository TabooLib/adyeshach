package ink.ptms.adyeshach.common.api

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntitySize
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.type.AdyEntity
import org.bukkit.entity.EntityType

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.AdyeshachEntityTypeHandler
 *
 * @author 坏黑
 * @since 2022/6/16 18:31
 */
interface AdyeshachEntityTypeHandler {

    /**
     * 获取 Bukkit 对应的实体类型
     */
    fun getBukkitEntityType(entityType: EntityTypes): EntityType

    /**
     * 获取 Bukkit 对应的实体序号
     */
    fun getBukkitEntityId(entityType: EntityTypes): Int

    /**
     * 获取 Bukkit 对应的内部名称
     */
    fun getBukkitEntityInternalName(entityType: EntityTypes): String?

    /**
     * 通过与 AdyEntity 类似的接口类获取对应实体类型
     */
    fun getEntityTypeFromAdyClass(clazz: Class<out AdyEntity>): EntityTypes?

    /**
     * 获取实体所对应的原版大小
     */
    fun getEntitySize(entityType: EntityTypes): EntitySize

    /**
     * 获取实体寻路类型
     */
    fun getEntityPathType(entityType: EntityTypes): PathType

    /**
     * 创建实体实例
     */
    fun getEntityInstance(entityType: EntityTypes): EntityInstance
}