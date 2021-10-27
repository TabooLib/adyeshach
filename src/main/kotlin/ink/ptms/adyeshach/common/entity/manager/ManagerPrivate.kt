package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.ControllerNone
import ink.ptms.adyeshach.common.entity.manager.database.Database
import ink.ptms.adyeshach.common.util.serializer.UnknownWorldException
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

/**
 * @author sky
 * @since 2020-08-14 14:25
 */
class ManagerPrivate(val player: String, val database: Database): Manager() {

    val activeEntity = CopyOnWriteArrayList<EntityInstance>()

    override fun onEnable() {
        activeEntity.clear()
        val player = Bukkit.getPlayerExact(player)!!
        val file = database.pull(player)
        val conf = file.getConfigurationSection("AdyeshachNPC") ?: return
        conf.getKeys(false).forEach {
            try {
                val entity = AdyeshachAPI.fromYaml(conf.getConfigurationSection(it)!!) ?: return@forEach
                if (entity.entityType.bukkitType == null) {
                    println("Entity \"${entity.entityType.name}\" not supported this minecraft version.")
                } else {
                    entity.manager = this
                    entity.addViewer(player)
                    activeEntity.add(entity)
                }
            } catch (ex: UnknownWorldException) {
            }
        }
    }

    override fun onDisable() {
        activeEntity.forEach {
            it.destroy()
        }
    }

    override fun onSave() {
        val player = Bukkit.getPlayerExact(player) ?: return
        val file = database.pull(player)
        activeEntity.forEach {
            it.unregisterController(ControllerNone::class.java)
            it.toYaml(file.createSection("AdyeshachNPC.${it.uniqueId}"))
        }
        database.push(player)
    }

    override fun create(entityTypes: EntityTypes, location: Location, function: Consumer<EntityInstance>): EntityInstance {
        return create(entityTypes, location, listOf(Bukkit.getPlayerExact(player)!!), function).run {
            activeEntity.add(this)
            this
        }
    }

    override fun remove(entityInstance: EntityInstance) {
        val player = Bukkit.getPlayerExact(player)!!
        val file = database.pull(player)
        file.set("AdyeshachNPC.${entityInstance.uniqueId}", null)
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