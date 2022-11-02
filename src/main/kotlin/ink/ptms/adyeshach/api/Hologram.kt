package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

abstract class Hologram<T> {

    protected val map = ConcurrentHashMap<Int, T>()

    fun create(location: Location, content: List<String>) {
        content.forEachIndexed { index, line ->
            map[index] = create(location.clone().add(0.0, (((content.size - 1) - index) * 0.3), 0.0), line)
        }
    }

    fun create(player: Player, location: Location, content: List<String>) {
        content.forEachIndexed { index, line ->
            map[index] = create(player, location.clone().add(0.0, (((content.size - 1) - index) * 0.3), 0.0), line)
        }
    }

    fun teleport(location: Location) {
        map.forEach { (index, obj) ->
            teleport(obj, location.clone().add(0.0, (((map.size - 1) - index) * 0.3), 0.0))
        }
    }

    fun update(content: List<String>) {
        content.forEachIndexed { index, line ->
            if (index < map.size) {
                update(map[index]!!, line)
            }
        }
    }

    fun delete() {
        map.forEach { delete(it.value) }
    }

    protected abstract fun create(location: Location, line: String): T

    protected abstract fun create(player: Player, location: Location, line: String): T

    protected abstract fun update(obj: T, line: String)

    protected abstract fun teleport(obj: T, location: Location)

    protected abstract fun delete(obj: T)

    open class AdyeshachImpl : Hologram<AdyArmorStand>() {

        override fun create(location: Location, line: String): AdyArmorStand {
            return AdyeshachAPI.getEntityManagerPublicTemporary().create(EntityTypes.ARMOR_STAND, location) { apply(it, line) } as AdyArmorStand
        }

        override fun create(player: Player, location: Location, line: String): AdyArmorStand {
            return AdyeshachAPI.getEntityManagerPrivateTemporary(player).create(EntityTypes.ARMOR_STAND, location) { apply(it, line) } as AdyArmorStand
        }

        override fun update(obj: AdyArmorStand, line: String) {
            if (!obj.isDeleted) {
                obj.setCustomName(line)
                obj.setCustomNameVisible(line.isNotEmpty())
            }
        }

        override fun teleport(obj: AdyArmorStand, location: Location) {
            if (!obj.isDeleted) {
                obj.teleport(location)
            }
        }

        override fun delete(obj: AdyArmorStand) {
            obj.delete()
        }

        fun apply(it: EntityInstance, line: String) {
            val npc = it as AdyArmorStand
            npc.setSmall(true)
            npc.setMarker(true)
            npc.setBasePlate(false)
            npc.setInvisible(true)
            npc.setCustomName(line)
            npc.setCustomNameVisible(line.isNotEmpty())
        }
    }
}