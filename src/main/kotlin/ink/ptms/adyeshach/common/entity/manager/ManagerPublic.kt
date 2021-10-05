package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.AdyeshachSettings
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.ControllerNone
import ink.ptms.adyeshach.common.util.serializer.UnknownWorldException
import org.bukkit.Bukkit
import org.bukkit.Location
import taboolib.common.io.newFile
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

/**
 * @author sky
 * @since 2020-08-14 19:08
 */
class ManagerPublic : Manager() {

    val activeEntity = CopyOnWriteArrayList<EntityInstance>()

    override fun isPublic(): Boolean {
        return true
    }

    override fun onEnable() {
        activeEntity.clear()
        File(Adyeshach.plugin.dataFolder, "npc").listFiles()?.filter { file -> file.name.endsWith(".json") }?.forEach { file ->
            try {
                val entity = AdyeshachAPI.fromJson(file.readText(StandardCharsets.UTF_8)) ?: return@forEach
                if (entity.entityType.bukkitType == null) {
                    println("Entity \"${entity.entityType.name}\" not supported this minecraft version.")
                } else {
                    entity.manager = this
                    activeEntity.add(entity)
                    if (entity.visibleAfterLoaded) {
                        Bukkit.getOnlinePlayers().forEach { p -> entity.addViewer(p) }
                    }
                }
            } catch (ex: UnknownWorldException) {
                if (AdyeshachSettings.isSpecifiedWorld(ex.world)) {
                    file.delete()
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
            entity.unregisterController(ControllerNone::class.java)
            newFile(Adyeshach.plugin.dataFolder, "npc/${entity.uniqueId}.json").writeText(entity.toJson())
        }
    }

    override fun create(entityTypes: EntityTypes, location: Location, function: Consumer<EntityInstance>): EntityInstance {
        return create(entityTypes, location, Bukkit.getOnlinePlayers().toList(), function).run {
            activeEntity.add(this)
            this
        }
    }

    override fun remove(entityInstance: EntityInstance) {
        val file = File(Adyeshach.plugin.dataFolder, "npc/${entityInstance.uniqueId}.json")
        if (file.exists()) {
            file.copyTo(File(Adyeshach.plugin.dataFolder, "npc/trash/${entityInstance.uniqueId}.json"))
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