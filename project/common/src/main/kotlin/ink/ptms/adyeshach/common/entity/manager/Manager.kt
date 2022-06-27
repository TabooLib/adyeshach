package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.function.Consumer
import java.util.function.Function

/**
 * @author sky
 * @since 2020-08-14 14:24
 */
interface Manager {

    /**
     * 是否为公开单位管理器
     */
    fun isPublic(): Boolean

    /**
     * 创建单位
     *
     * @param entityTypes 单位类型
     * @param location 坐标
     */
    fun create(entityTypes: EntityTypes, location: Location): EntityInstance

    /**
     * 创建单位
     *
     * @param entityTypes 单位类型
     * @param location 位置
     * @param callback 创建前回调函数
     */
    fun create(entityTypes: EntityTypes, location: Location, callback: Consumer<EntityInstance>): EntityInstance

    /**
     * 创建单位
     *
     * @param entityTypes 单位类型
     * @param location 坐标
     * @param player 可视玩家列表
     */
    fun create(entityTypes: EntityTypes, location: Location, player: List<Player>): EntityInstance

    /**
     * 创建单位
     *
     * @param entityTypes 单位类型
     * @param location 坐标
     * @param player 可视玩家列表
     * @param function 提前操作函数
     */
    fun create(entityTypes: EntityTypes, location: Location, player: List<Player>, function: Consumer<EntityInstance>): EntityInstance

    /**
     * 从管理器中删除单位
     */
    fun delete(entityInstance: EntityInstance)

    /**
     * 获取单位管理器中的所有单位
     */
    fun getEntities(): List<EntityInstance>

    /**
     * 根据要求获取单位管理器中的单位
     */
    fun getEntity(match: Function<EntityInstance, Boolean>): EntityInstance?

    /**
     * 获取单位管理器中的所有单位，并进行过滤
     */
    fun getEntities(filter: Function<EntityInstance, Boolean>): List<EntityInstance>

    /**
     * 通过 id 获取单位
     */
    fun getEntityById(id: String): List<EntityInstance>

    /**
     * 通过 id 获取单位，并进行过滤
     */
    fun getEntityById(id: String, filter: Function<EntityInstance, Boolean>): List<EntityInstance>

    /**
     * 通过 uuid 获取单位
     */
    fun getEntityByUniqueId(id: String): EntityInstance?
}

/**
 * [Manager#create] 衍生方法
 */
inline fun <reified T : AdyEntity> Manager.create(location: Location): T {
    val type = Adyeshach.api().getEntityTypeHandler().getEntityTypeFromAdyClass(T::class.java) ?: error("Unsupported ${T::class.java.name}")
    return create(type, location) as T
}

/**
 * [Manager#create] 衍生方法
 */
inline fun <reified T : AdyEntity> Manager.create(location: Location, callback: Consumer<EntityInstance>): T {
    val type = Adyeshach.api().getEntityTypeHandler().getEntityTypeFromAdyClass(T::class.java) ?: error("Unsupported ${T::class.java.name}")
    return create(type, location, callback) as T
}

/**
 * [Manager#create] 衍生方法
 */
inline fun <reified T : AdyEntity> Manager.create(location: Location, player: List<Player>): T {
    val type = Adyeshach.api().getEntityTypeHandler().getEntityTypeFromAdyClass(T::class.java) ?: error("Unsupported ${T::class.java.name}")
    return create(type, location, player) as T
}

/**
 * [Manager#create] 衍生方法
 */
inline fun <reified T : AdyEntity> Manager.create(location: Location, player: List<Player>, callback: Consumer<EntityInstance>): T {
    val type = Adyeshach.api().getEntityTypeHandler().getEntityTypeFromAdyClass(T::class.java) ?: error("Unsupported ${T::class.java.name}")
    return create(type, location, player, callback) as T
}