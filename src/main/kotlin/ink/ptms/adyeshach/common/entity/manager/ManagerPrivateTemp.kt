package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

/**
 * @Author sky
 * @Since 2020-08-14 14:25
 */
class ManagerPrivateTemp(val player: String): Manager() {

    val activeEntity = CopyOnWriteArrayList<EntityInstance>()

    override fun onEnable() {
    }

    override fun onDisable() {
        activeEntity.forEach {
            it.destroy()
        }
    }

    override fun onSave() {
    }

    override fun create(entityTypes: EntityTypes, location: Location, function: Consumer<EntityInstance>): EntityInstance {
        return create(entityTypes, location, listOf(Bukkit.getPlayerExact(player)!!), function).run {
            activeEntity.add(this)
            this
        }
    }

    override fun remove(entityInstance: EntityInstance) {
        activeEntity.remove(entityInstance)
    }

    override fun addEntity(entityInstance: EntityInstance) {
        activeEntity.add(entityInstance)
    }

    override fun removeEntity(entityInstance: EntityInstance) {
        activeEntity.remove(entityInstance)
    }

    override fun getEntities(): List<EntityInstance> {
        return activeEntity
    }

    override fun getEntityById(id: String): List<EntityInstance> {
        return activeEntity.filter { it.id == id }
    }

    override fun getEntityByUniqueId(id: String): EntityInstance? {
        return activeEntity.firstOrNull { it.uniqueId == id }
    }
}