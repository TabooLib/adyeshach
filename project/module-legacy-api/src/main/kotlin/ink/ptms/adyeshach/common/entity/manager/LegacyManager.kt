package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.manager.Manager
import org.bukkit.Location
import java.util.function.Consumer

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.manager.LegacyManager
 *
 * @author 坏黑
 * @since 2023/1/3 16:51
 */
@Deprecated("Outdated but usable")
class LegacyManager(val v2: Manager) : ink.ptms.adyeshach.common.entity.manager.Manager() {

    override fun v2(): Manager {
        return v2
    }

    override fun create(entityTypes: EntityTypes, location: Location, function: Consumer<EntityInstance>): EntityInstance {
        return entityTypes.toV1(v2.create(entityTypes.v2(), location) { function.accept(entityTypes.toV1(it)) })
    }

    override fun remove(entityInstance: EntityInstance) {
        v2.remove(entityInstance.v2)
    }

    override fun addEntity(entityInstance: EntityInstance) {
        v2.add(entityInstance.v2)
    }

    override fun removeEntity(entityInstance: EntityInstance) {
        v2.remove(entityInstance.v2)
    }

    override fun getEntities(): List<EntityInstance> {
        return v2.getEntities().mapNotNull { EntityTypes.adapt(it) }
    }

    override fun getEntityById(id: String): List<EntityInstance> {
        return v2.getEntityById(id).mapNotNull { EntityTypes.adapt(it) }
    }

    override fun getEntityByUniqueId(id: String): EntityInstance? {
        return v2.getEntityByUniqueId(id)?.let { EntityTypes.adapt(it) }
    }
}