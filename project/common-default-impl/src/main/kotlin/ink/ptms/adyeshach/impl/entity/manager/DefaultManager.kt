package ink.ptms.adyeshach.impl.entity.manager

import ink.ptms.adyeshach.api.event.AdyeshachEntityCreateEvent
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.TickService
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.entity.manager.ManagerService
import ink.ptms.adyeshach.common.util.safeDistance
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.platform.util.onlinePlayers
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.manager.DefaultManager
 *
 * @author 坏黑
 * @since 2022/6/28 00:19
 */
open class DefaultManager : Manager, ManagerService, TickService {

    val prepareTicks = LinkedList<Consumer<EntityInstance>>()
    val activeEntity = LinkedList<EntityInstance>()
    val pushList = CopyOnWriteArrayList<EntityInstance>()
    val removeList = CopyOnWriteArrayList<EntityInstance>()

    open fun getPlayers(): List<Player> {
        return onlinePlayers
    }

    override fun create(entityTypes: EntityTypes, location: Location): EntityInstance {
        return create(entityTypes, location, getPlayers()) { }
    }

    override fun create(entityTypes: EntityTypes, location: Location, callback: Consumer<EntityInstance>): EntityInstance {
        return create(entityTypes, location, getPlayers(), callback)
    }

    override fun create(entityTypes: EntityTypes, location: Location, player: List<Player>): EntityInstance {
        return create(entityTypes, location, player) { }
    }

    override fun create(entityTypes: EntityTypes, location: Location, player: List<Player>, function: Consumer<EntityInstance>): EntityInstance {
        val typeHandler = Adyeshach.api().getEntityTypeHandler()
        typeHandler.getBukkitEntityType(entityTypes)
        // 创建实体类
        val entityInstance = typeHandler.getEntityInstance(entityTypes)
        // 注册管理器
        entityInstance.manager = this
        // 添加观察者
        entityInstance.viewPlayers.viewers.addAll(player.map { it.name })
        entityInstance.viewPlayers.visible.addAll(player.filter { it.location.safeDistance(location) < entityInstance.visibleDistance }.map { it.name })
        // 唤起事件
        val event = AdyeshachEntityCreateEvent(entityInstance, location)
        if (event.call()) {
            function.accept(entityInstance)
            // 生成实体
            entityInstance.spawn(event.location)
            // 注册实体
            add(entityInstance)
        }
        return entityInstance
    }

    override fun add(entity: EntityInstance) {
        pushList.add(entity)
    }

    override fun delete(entityInstance: EntityInstance) {
        // 移动到删除队列，下个游戏刻删除
        removeList.remove(entityInstance)
    }

    override fun getEntities(): List<EntityInstance> {
        return activeEntity
    }

    override fun getEntities(filter: Predicate<EntityInstance>): List<EntityInstance> {
        return activeEntity.filter { filter.test(it) }
    }

    override fun getEntity(match: Predicate<EntityInstance>): EntityInstance? {
        return activeEntity.firstOrNull { match.test(it) }
    }

    override fun getEntityById(id: String): List<EntityInstance> {
        return activeEntity.filter { it.id == id }
    }

    override fun getEntityById(id: String, filter: Predicate<EntityInstance>): List<EntityInstance> {
        return activeEntity.filter { it.id == id && filter.test(it) }
    }

    override fun getEntityByUniqueId(id: String): EntityInstance? {
        return activeEntity.firstOrNull { it.uniqueId == id }
    }

    override fun isPublic(): Boolean {
        return true
    }

    override fun isTemporary(): Boolean {
        return true
    }

    override fun onEnable() {
    }

    override fun onDisable() {
        activeEntity.forEach { it.despawn() }
        activeEntity.clear()
    }

    override fun onSave() {
    }

    override fun onTick() {
        // 修正列表
        updateActiveEntityList()
        // 处理列表
        activeEntity.forEach {
            if (it is TickService) {
                prepareTicks.forEach { p -> p.accept(it) }
                it.onTick()
            }
        }
    }

    override fun prepareTick(callback: Consumer<EntityInstance>) {
        prepareTicks += callback
    }

    fun updateActiveEntityList() {
        if (pushList.isNotEmpty()) {
            synchronized(pushList) {
                activeEntity.addAll(pushList)
                pushList.clear()
            }
        }
        if (removeList.isNotEmpty()) {
            synchronized(removeList) {
                activeEntity.removeAll(removeList)
                removeList.clear()
            }
        }
    }
}