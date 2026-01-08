package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.entity.Player
import taboolib.platform.util.onlinePlayers
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.DefaultManager
 *
 * @author 坏黑
 * @since 2022/6/28 00:19
 */
open class DefaultManager : BaseManager() {

    /** 所有实体 */
    val activeEntity: MutableSet<EntityInstance> = ConcurrentHashMap.newKeySet()
    val activeEntityByUniqueId = ConcurrentHashMap<String, EntityInstance>()

    /** 
     * 可 tick 的实体索引（有可见玩家的实体）
     */
    val tickableEntities: MutableSet<EntityInstance> = ConcurrentHashMap.newKeySet()

    override fun getPlayers(): List<Player> {
        return onlinePlayers
    }

    override fun add(entity: EntityInstance) {
        activeEntity.add(entity)
        activeEntityByUniqueId[entity.uniqueId] = entity
        // 注册可见性变化回调，维护 tickableEntities 索引
        setupTickableCallback(entity)
        // 如果已有可见玩家，立即加入 tickable 列表
        if (entity.viewPlayers.hasVisiblePlayer()) {
            tickableEntities.add(entity)
        }
    }

    override fun remove(entityInstance: EntityInstance) {
        activeEntity.remove(entityInstance)
        activeEntityByUniqueId.remove(entityInstance.uniqueId)
        tickableEntities.remove(entityInstance)
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
        // 优化：只遍历有可见玩家的实体，而不是全部实体
        tickableEntities.forEach {
            // 事件处理
            if (DefaultAdyeshachAPI.localEventBus.callTick(it)) {
                it.onTick()
            }
        }
    }

    /**
     * 设置实体的可见性变化回调
     * 当实体有可见玩家时加入 tickableEntities，无可见玩家时移除
     */
    protected open fun setupTickableCallback(entity: EntityInstance) {
        if (entity is DefaultEntityInstance) {
            val viewPlayers = entity.viewPlayers
            // 当有玩家变为可见时
            viewPlayers.onVisibleAdded {
                tickableEntities.add(entity)
            }
            // 当玩家变为不可见时，检查是否还有其他可见玩家
            viewPlayers.onVisibleRemoved {
                if (!viewPlayers.hasVisiblePlayer()) {
                    tickableEntities.remove(entity)
                }
            }
        }
    }
}