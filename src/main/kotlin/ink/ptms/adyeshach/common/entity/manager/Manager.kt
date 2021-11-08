package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.api.event.AdyeshachEntityCreateEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 * @author sky
 * @since 2020-08-14 14:24
 */
abstract class Manager {

    /**
     * 创建单位
     * @param entityTypes 单位类型
     * @param location 位置
     * @param function 提前操作函数
     */
    abstract fun create(entityTypes: EntityTypes, location: Location, function: Consumer<EntityInstance>): EntityInstance

    /**
     * 创建单位
     * @param entityTypes 单位类型
     * @param location 坐标
     */
    fun create(entityTypes: EntityTypes, location: Location): EntityInstance {
        return create(entityTypes, location) { }
    }

    /**
     * 创建单位
     * @param entityTypes 单位类型
     * @param location 坐标
     * @param player 可视玩家列表
     */
    fun create(entityTypes: EntityTypes, location: Location, player: List<Player>): EntityInstance {
        return create(entityTypes, location, player) { }
    }

    /**
     * 创建单位
     * @param entityTypes 单位类型
     * @param location 坐标
     * @param player 可视玩家列表
     * @param function 提前操作函数
     */
    fun create(entityTypes: EntityTypes, location: Location, player: List<Player>, function: Consumer<EntityInstance>): EntityInstance {
        if (entityTypes.bukkitType == null) {
            error("Entity \"${entityTypes.name}\" not supported this minecraft version.")
        }
        val entityInstance = entityTypes.newInstance()
        function.accept(entityInstance)
        entityInstance.manager = this
        entityInstance.viewPlayers.viewers.addAll(player.map { it.name })
        entityInstance.viewPlayers.visible.addAll(player.filter {
            it.world == location.world && it.location.distance(location) < entityInstance.visibleDistance
        }.map {
            it.name
        })
        val event = AdyeshachEntityCreateEvent(entityInstance, location)
        event.call()
        if (event.isCancelled) {
            return entityInstance
        }
        entityInstance.spawn(event.location)
        return entityInstance
    }

    /**
     * 移除一个单位
     */
    abstract fun remove(entityInstance: EntityInstance)

    /**
     * 添加一个单位到管理器中托管
     */
    abstract fun addEntity(entityInstance: EntityInstance)

    /**
     * 从管理器中移除一个单位托管
     */
    abstract fun removeEntity(entityInstance: EntityInstance)

    /**
     * 获取单位管理器中的所有单位
     */
    abstract fun getEntities(): List<EntityInstance>

    /**
     * 通过 id 获取单位
     */
    abstract fun getEntityById(id: String): List<EntityInstance>

    /**
     * 通过 uuid 获取单位
     */
    abstract fun getEntityByUniqueId(id: String): EntityInstance?

    /**
     * 是否为公开单位管理器
     */
    open fun isPublic(): Boolean {
        return false
    }

    open fun onEnable() {
    }

    open fun onDisable() {
    }

    open fun onSave() {
    }

    open fun onTick() {
        getEntities().forEach { it.onTick() }
    }
}