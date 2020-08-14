package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.internal.database.Database
import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * @Author sky
 * @Since 2020-08-14 14:25
 */
class ManagerPrivate(val player: String, val database: Database): Manager() {

    override fun onLoad() {
        val player = Bukkit.getPlayerExact(player)!!
        val file = database.download(player)
        val conf = file.getConfigurationSection("AdyeshachNPC") ?: return
        conf.getKeys(false).forEach {
            val entity = AdyeshachAPI.fromYaml(conf.getConfigurationSection(it)!!, player) ?: return@forEach
            if (entity.entityType.bukkitType == null) {
                println("Entity \"${entity.entityType.name}\" not supported this minecraft version.")
            } else {
                entity.manager = this
                entity.addViewer(player)
                AdyeshachAPI.activeEntity.add(entity)
            }
        }
    }

    override fun onSave() {
        val player = Bukkit.getPlayerExact(player)!!
        val file = database.download(player)
        AdyeshachAPI.activeEntity.forEach {
            it.toYaml(file.createSection("AdyeshachNPC.${it.uniqueId}"))
        }
        database.upload(player)
    }

    override fun create(entityTypes: EntityTypes, location: Location, function: (EntityInstance) -> (Unit)): EntityInstance {
        return create(entityTypes, location, listOf(Bukkit.getPlayerExact(player)!!), function).run {
            AdyeshachAPI.activeEntity.add(this)
            this
        }
    }

    override fun remove(entityInstance: EntityInstance) {
        val player = Bukkit.getPlayerExact(player)!!
        val file = database.download(player)
        file.set("AdyeshachNPC.${entityInstance.uniqueId}", null)
        AdyeshachAPI.activeEntity.remove(entityInstance)
    }
}