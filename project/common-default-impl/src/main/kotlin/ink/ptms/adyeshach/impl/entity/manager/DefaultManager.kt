package ink.ptms.adyeshach.impl.entity.manager

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.TickService
import ink.ptms.adyeshach.common.entity.manager.Manager
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.function.Consumer
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.manager.DefaultManager
 *
 * @author 坏黑
 * @since 2022/6/28 00:19
 */
open class DefaultManager : Manager, TickService {

    override fun create(entityTypes: EntityTypes, location: Location): EntityInstance {
        TODO("Not yet implemented")
    }

    override fun create(entityTypes: EntityTypes, location: Location, callback: Consumer<EntityInstance>): EntityInstance {
        TODO("Not yet implemented")
    }

    override fun create(entityTypes: EntityTypes, location: Location, player: List<Player>): EntityInstance {
        TODO("Not yet implemented")
    }

    override fun create(entityTypes: EntityTypes, location: Location, player: List<Player>, function: Consumer<EntityInstance>): EntityInstance {
        TODO("Not yet implemented")
    }

    override fun delete(entityInstance: EntityInstance) {
        TODO("Not yet implemented")
    }

    override fun getEntities(): List<EntityInstance> {
        TODO("Not yet implemented")
    }

    override fun getEntities(filter: Function<EntityInstance, Boolean>): List<EntityInstance> {
        TODO("Not yet implemented")
    }

    override fun getEntity(match: Function<EntityInstance, Boolean>): EntityInstance? {
        TODO("Not yet implemented")
    }

    override fun getEntityById(id: String): List<EntityInstance> {
        TODO("Not yet implemented")
    }

    override fun getEntityById(id: String, filter: Function<EntityInstance, Boolean>): List<EntityInstance> {
        TODO("Not yet implemented")
    }

    override fun getEntityByUniqueId(id: String): EntityInstance? {
        TODO("Not yet implemented")
    }

    override fun isPublic(): Boolean {
        return true
    }

    override fun onTick() {
        getEntities().forEach { (it as? TickService)?.onTick() }
    }
}