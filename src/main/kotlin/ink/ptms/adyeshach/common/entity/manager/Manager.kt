package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.AdyeshachEntityCreateEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @Author sky
 * @Since 2020-08-14 14:24
 */
abstract class Manager {

    abstract fun create(entityTypes: EntityTypes, location: Location, function: (EntityInstance) -> (Unit)): EntityInstance

    abstract fun remove(entityInstance: EntityInstance)

    open fun onLoad() {

    }

    open fun onSave() {

    }

    open fun onTick() {
        AdyeshachAPI.activeEntity.forEach { it.onTick() }
    }

    open fun create(entityTypes: EntityTypes, location: Location): EntityInstance {
        return create(entityTypes, location) { }
    }

    protected fun create(entityTypes: EntityTypes, location: Location, player: List<Player>): EntityInstance {
        return create(entityTypes, location, player) { }
    }

    protected fun create(entityTypes: EntityTypes, location: Location, player: List<Player>, function: (EntityInstance) -> (Unit)): EntityInstance {
        if (entityTypes.bukkitType == null) {
            throw RuntimeException("Entity \"${entityTypes.name}\" not supported this minecraft version.")
        }
        val entityInstance = entityTypes.entityBase.newInstance()
        function.invoke(entityInstance)
        entityInstance.manager = this
        entityInstance.viewPlayers.viewers.addAll(player.map { it.name })
        val event = AdyeshachEntityCreateEvent(entityInstance, location).call()
        if (event.isCancelled) {
            return entityInstance
        }
        entityInstance.spawn(event.location)
        return entityInstance
    }
}