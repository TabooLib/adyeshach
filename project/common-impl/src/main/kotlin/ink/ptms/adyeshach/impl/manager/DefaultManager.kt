package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.TickService
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import org.bukkit.entity.Player
import taboolib.platform.util.onlinePlayers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.DefaultManager
 *
 * @author 坏黑
 * @since 2022/6/28 00:19
 */
open class DefaultManager : BaseManager() {

    val activeEntity = ConcurrentSkipListSet(Comparator.comparing(EntityInstance::uniqueId))
    val activeEntityByUniqueId = ConcurrentHashMap<String, EntityInstance>()

    override fun getPlayers(): List<Player> {
        return onlinePlayers
    }

    override fun add(entity: EntityInstance) {
        activeEntity.add(entity)
        activeEntityByUniqueId[entity.uniqueId] = entity
    }

    override fun remove(entityInstance: EntityInstance) {
        activeEntity.remove(entityInstance)
        activeEntityByUniqueId.remove(entityInstance.uniqueId)
    }

    override fun getEntities(): List<EntityInstance> {
        return ArrayList(activeEntity)
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
        return activeEntityByUniqueId[id]
    }

    override fun isPublic(): Boolean {
        return true
    }

    override fun isTemporary(): Boolean {
        return true
    }

    override fun onDisable() {
        activeEntity.forEach { it.despawn() }
    }

    override fun checkVisible() {
        activeEntity.forEach { it.checkVisible() }
    }

    override fun onTick() {
        // 处理列表
        activeEntity.forEach {
            // 事件处理
            if (DefaultAdyeshachAPI.localEventBus.callTick(it)) {
                it.onTick()
            }
        }
    }
}