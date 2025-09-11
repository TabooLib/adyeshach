@file:Suppress("UNCHECKED_CAST")

package ink.ptms.adyeshach.core.entity.manager

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * @author sky
 * @since 2020-08-14 14:24
 */
interface Manager {

    /**
     * 是否有效
     */
    fun isValid(): Boolean

    /**
     * 是否为公开单位管理器
     */
    fun isPublic(): Boolean

    /**
     * 是否为临时单位管理器（不含持久化逻辑）
     */
    fun isTemporary(): Boolean

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
     * 添加单位
     */
    fun add(entity: EntityInstance)

    /**
     * 从管理器中删除单位
     */
    fun remove(entityInstance: EntityInstance)

    /**
     * 获取单位管理器中的所有单位
     */
    fun getEntities(): List<EntityInstance>

    /**
     * 根据要求获取单位管理器中的单位
     */
    fun getEntity(match: Predicate<EntityInstance>): EntityInstance?

    /**
     * 获取单位管理器中的所有单位，并进行过滤
     */
    fun getEntities(filter: Predicate<EntityInstance>): List<EntityInstance>

    /**
     * 获取单位管理器中 ID 相同的单位
     */
    fun getEntityById(id: String): List<EntityInstance>

    /**
     * 获取单位管理器中 ID 相同的单位，并进行过滤
     */
    fun getEntityById(id: String, filter: Predicate<EntityInstance>): List<EntityInstance>

    /**
     * 获取单位管理器中 UUID 相同的单位
     */
    fun getEntityByUniqueId(id: String): EntityInstance?

    /**
     * 从文件中加载单位
     */
    fun loadEntityFromFile(file: File): EntityInstance

    /**
     * 检查可见性
     */
    fun checkVisible()
}