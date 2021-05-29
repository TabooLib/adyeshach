package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.serializer.UnknownWorldException
import io.izzel.taboolib.kotlin.warning
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

    override fun isPublic(): Boolean {
        return true
    }

    override fun onEnable() {
        activeEntity.clear()
        File(Adyeshach.plugin.dataFolder, "npc").listFiles()?.filter { file -> file.name.endsWith(".json") }?.forEach { file ->
            Files.read(file) {
                try {
                    val entity = AdyeshachAPI.fromJson(it.lines().toArray().joinToString("\n")) ?: return@read
                    if (entity.entityType.bukkitType == null) {
                        println("Entity \"${entity.entityType.name}\" not supported this minecraft version.")
                    } else {
                        entity.manager = this
                        activeEntity.add(entity)
                        if (entity.alwaysVisible) {
                            Bukkit.getOnlinePlayers().forEach { p -> entity.addViewer(p) }
                        }
                    }
                } catch (ex: UnknownWorldException) {
                    if (Adyeshach.settings.isSpecifiedWorld(ex.world)) {
                        file.delete()
                    }
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
        return create(entityTypes, location, Bukkit.getOnlinePlayers().toList(), function).run {
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