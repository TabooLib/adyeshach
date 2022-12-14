package ink.ptms.adyeshach.impl.entity.manager

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.TickService
import ink.ptms.adyeshach.core.entity.manager.EventBus
import org.bukkit.entity.Player
import taboolib.platform.util.onlinePlayers
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.manager.DefaultManager
 *
 * @author 坏黑
 * @since 2022/6/28 00:19
 */
open class DefaultManager : BaseManager() {

    val defaultEventBus = DefaultEventBus()
    val activeEntity = LinkedList<EntityInstance>()
    val pushList = CopyOnWriteArrayList<EntityInstance>()
    val removeList = CopyOnWriteArrayList<EntityInstance>()

    override fun getPlayers(): List<Player> {
        return onlinePlayers
    }

    override fun add(entity: EntityInstance) {
        pushList.add(entity)
    }

    override fun delete(entityInstance: EntityInstance) {
        // 移动到删除队列，下个游戏刻删除
        removeList.add(entityInstance)
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

    override fun onDisable() {
        activeEntity.forEach { it.despawn() }
        activeEntity.clear()
    }

    override fun onTick() {
        // 修正列表
        updateActiveEntityList()
        // 处理列表
        activeEntity.forEach {
            // 实体受 Tick 服务影响 && 事件处理
            if (it is TickService && defaultEventBus.callTick(it)) {
                it.onTick()
            }
        }
    }

    override fun getEventBus(): EventBus? {
        return defaultEventBus
    }

    private fun updateActiveEntityList() {
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