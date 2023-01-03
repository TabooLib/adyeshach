package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 * @author sky
 * @since 2020-08-14 14:24
 */
@Deprecated("Outdated but usable")
abstract class Manager {

    abstract fun v2(): ink.ptms.adyeshach.core.entity.manager.Manager

    abstract fun create(entityTypes: EntityTypes, location: Location, function: Consumer<EntityInstance>): EntityInstance

    fun create(entityTypes: EntityTypes, location: Location): EntityInstance {
        return create(entityTypes, location) { }
    }

    fun create(entityTypes: EntityTypes, location: Location, player: List<Player>): EntityInstance {
        return create(entityTypes, location, player) { }
    }

    fun create(entityTypes: EntityTypes, location: Location, player: List<Player>, function: Consumer<EntityInstance>): EntityInstance {
        return entityTypes.toV1(v2().create(entityTypes.v2(), location, player) { function.accept(entityTypes.toV1(it)) })
    }

    abstract fun remove(entityInstance: EntityInstance)

    abstract fun addEntity(entityInstance: EntityInstance)

    abstract fun removeEntity(entityInstance: EntityInstance)

    abstract fun getEntities(): List<EntityInstance>

    abstract fun getEntityById(id: String): List<EntityInstance>

    abstract fun getEntityByUniqueId(id: String): EntityInstance?

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
    }
}