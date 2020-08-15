package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.util.Files
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @Author sky
 * @Since 2020-08-14 19:08
 */
class ManagerPublic : Manager() {

    val activeEntity = CopyOnWriteArrayList<EntityInstance>()

    override fun onEnable() {
        File(Adyeshach.plugin.dataFolder, "npc").listFiles()?.filter { file -> file.name.endsWith(".json") }?.forEach { file ->
            Files.read(file) {
                val entity = AdyeshachAPI.fromJson(it.lines().toArray().joinToString("\n"), null) ?: return@read
                if (entity.entityType.bukkitType == null) {
                    println("Entity \"${entity.entityType.name}\" not supported this minecraft version.")
                } else {
                    entity.manager = this
                    Bukkit.getOnlinePlayers().forEach { entity.addViewer(it) }
                    activeEntity.add(entity)
                }
            }
        }
    }

    override fun onDisable() {
        activeEntity.forEach {
            it.destroy()
        }
    }

    override fun onSave() {
        activeEntity.forEach { entity ->
            Files.write(Files.file(Adyeshach.plugin.dataFolder, "npc/${entity.uniqueId}.json")) {
                it.write(entity.toJson())
            }
        }
    }

    override fun create(entityTypes: EntityTypes, location: Location, function: (EntityInstance) -> Unit): EntityInstance {
        return create(entityTypes, location, location.world!!.players, function).run {
            activeEntity.add(this)
            this
        }
    }

    override fun remove(entityInstance: EntityInstance) {
        val file = File(Adyeshach.plugin.dataFolder, "npc/${entityInstance.uniqueId}.json")
        if (file.exists()) {
            Files.copy(file, Files.file(Adyeshach.plugin.dataFolder, "npc/trash/${entityInstance.uniqueId}.json"))
            file.delete()
        }
        activeEntity.remove(entityInstance)
    }

    override fun getEntities(): List<EntityInstance> {
        return activeEntity
    }

    override fun getEntity(id: String): List<EntityInstance> {
        return activeEntity.filter { it.id == id }
    }
}