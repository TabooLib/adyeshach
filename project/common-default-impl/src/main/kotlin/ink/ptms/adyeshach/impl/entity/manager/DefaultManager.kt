package ink.ptms.adyeshach.impl.entity.manager

import ink.ptms.adyeshach.api.event.AdyeshachEntityCreateEvent
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.TickService
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.entity.manager.ManagerService
import ink.ptms.adyeshach.common.util.safeDistance
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import java.util.function.Function

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
        return Bukkit.getOnlinePlayers().toList()
    }

    override fun create(entityTypes: EntityTypes, location: Location): EntityInstance {
        return create(entityTypes, location, getPlayers()) { }.also { pushList.add(it) }
    }

    override fun create(entityTypes: EntityTypes, location: Location, callback: Consumer<EntityInstance>): EntityInstance {
        return create(entityTypes, location, getPlayers(), callback).also { pushList.add(it) }
    }

    override fun create(entityTypes: EntityTypes, location: Location, player: List<Player>): EntityInstance {
        return create(entityTypes, location, player) { }.also { pushList.add(it) }
    }

    override fun create(entityTypes: EntityTypes, location: Location, player: List<Player>, function: Consumer<EntityInstance>): EntityInstance {
        val typeHandler = Adyeshach.api().getEntityTypeHandler()
        typeHandler.getBukkitEntityType(entityTypes)
        val entityInstance = typeHandler.getEntityInstance(entityTypes)
        entityInstance.manager = this
        entityInstance.viewPlayers.viewers.addAll(player.map { it.name })
        entityInstance.viewPlayers.visible.addAll(player.filter { it.location.safeDistance(location) < entityInstance.visibleDistance }.map { it.name })
        val event = AdyeshachEntityCreateEvent(entityInstance, location)
        if (event.call()) {
            function.accept(entityInstance)
            entityInstance.spawn(event.location)
        }
        return entityInstance
    }

    override fun add(entity: EntityInstance) {
        pushList.add(entity)
    }

    override fun delete(entityInstance: EntityInstance) {
        removeList.remove(entityInstance)
    }

    override fun getEntities(): List<EntityInstance> {
        return activeEntity
    }

    override fun getEntities(filter: Function<EntityInstance, Boolean>): List<EntityInstance> {
        return activeEntity.filter { filter.apply(it) }
    }

    override fun getEntity(match: Function<EntityInstance, Boolean>): EntityInstance? {
        return activeEntity.firstOrNull { match.apply(it) }
    }

    override fun getEntityById(id: String): List<EntityInstance> {
        return activeEntity.filter { it.id == id }
    }

    override fun getEntityById(id: String, filter: Function<EntityInstance, Boolean>): List<EntityInstance> {
        return activeEntity.filter { it.id == id && filter.apply(it) }
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
        activeEntity.forEach { it.destroy() }
        activeEntity.clear()
    }

    override fun onSave() {
    }

    override fun onTick() {
        if (pushList.isNotEmpty()) {
            activeEntity += pushList
            pushList.clear()
        }
        if (removeList.isNotEmpty()) {
            activeEntity -= removeList
            removeList.clear()
        }
        getEntities().forEach {
            if (it is TickService) {
                prepareTicks.forEach { p -> p.accept(it) }
                it.onTick()
            }
        }
    }

    override fun prepareTick(callback: Consumer<EntityInstance>) {
        prepareTicks += callback
    }
}