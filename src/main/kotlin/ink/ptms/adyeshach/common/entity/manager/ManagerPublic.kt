package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.util.Files
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File

/**
 * @Author sky
 * @Since 2020-08-14 19:08
 */
class ManagerPublic : Manager() {

    override fun onLoad() {
        File(Adyeshach.plugin.dataFolder, "npc").listFiles()?.filter { file -> file.name.endsWith(".json") }?.forEach { file ->
            Files.read(file) {
                val entity = AdyeshachAPI.fromJson(it.lines().toArray().joinToString("\n"), null) ?: return@read
                if (entity.entityType.bukkitType == null) {
                    println("Entity \"${entity.entityType.name}\" not supported this minecraft version.")
                } else {
                    entity.manager = this
                    entity.viewPlayers
                    AdyeshachAPI.activeEntity.add(entity)
                }
            }
        }
    }

    override fun onSave() {
        AdyeshachAPI.activeEntity.forEach { entity ->
            Files.write(File(File(Adyeshach.plugin.dataFolder, "npc"), "${entity.uniqueId}.json")) {
                it.write(entity.toJson())
            }
        }
    }

    override fun create(entityTypes: EntityTypes, location: Location, function: (EntityInstance) -> Unit): EntityInstance {
        return create(entityTypes, location, location.world!!.players, function).run {
            AdyeshachAPI.activeEntity.add(this)
            this
        }
    }

    override fun remove(entityInstance: EntityInstance) {
        File(File(Adyeshach.plugin.dataFolder, "npc"), "${entityInstance.uniqueId}.json").delete()
    }
}