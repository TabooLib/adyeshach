package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.AdyeshachAPI
import ink.ptms.adyeshach.common.api.AdyeshachEntityFinder
import ink.ptms.adyeshach.common.entity.ClientEntity
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.manager.ManagerType
import ink.ptms.adyeshach.common.util.safeDistance
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.DefaultAdyeshachEntityFinder
 *
 * @author 坏黑
 * @since 2022/6/19 00:15
 */
class DefaultAdyeshachEntityFinder : AdyeshachEntityFinder {

    val api: AdyeshachAPI
        get() = Adyeshach.api()

    override fun getEntity(player: Player?, match: Predicate<EntityInstance>): EntityInstance? {
        api.getPublicEntityManager().getEntity(match)?.let { return it }
        api.getPublicEntityManager(ManagerType.TEMPORARY).getEntity(match)?.let { return it }
        if (player != null) {
            api.getPrivateEntityManager(player).getEntity(match)?.let { return it }
            api.getPrivateEntityManager(player, ManagerType.TEMPORARY).getEntity(match)?.let { return it }
        }
        return null
    }

    override fun getEntities(player: Player?, filter: Predicate<EntityInstance>): List<EntityInstance> {
        val entity = LinkedList<EntityInstance>()
        entity.addAll(api.getPublicEntityManager().getEntities(filter))
        entity.addAll(api.getPublicEntityManager(ManagerType.TEMPORARY).getEntities(filter))
        if (player != null) {
            entity.addAll(api.getPrivateEntityManager(player).getEntities(filter))
            entity.addAll(api.getPrivateEntityManager(player, ManagerType.TEMPORARY).getEntities(filter))
        }
        return entity
    }

    override fun getVisibleEntities(player: Player, filter: Predicate<EntityInstance>): List<EntityInstance> {
        return getEntities(player) { it.isViewer(player) && it.getLocation().safeDistance(player.location) <= it.visibleDistance && filter.test(it) }
    }

    override fun getEntitiesFromId(id: String, player: Player?): List<EntityInstance> {
        return getEntities(null) { it.id == id }
    }

    override fun getEntitiesFromIdOrUniqueId(id: String, player: Player?): List<EntityInstance> {
        return getEntities(null) { it.id == id || it.uniqueId == id }
    }

    override fun getEntityFromEntityId(id: Int, player: Player?): EntityInstance? {
        return getEntity(null) { it.index == id }
    }

    override fun getEntityFromUniqueId(id: String, player: Player?): EntityInstance? {
        return getEntity(null) { it.uniqueId == id }
    }

    override fun getEntityFromClientUniqueId(id: UUID, player: Player): EntityInstance? {
        return clientEntityMap[player.name]?.values?.firstOrNull { it.clientId == id }?.entity
    }

    override fun getNearestEntity(player: Player, filter: Predicate<EntityInstance>): EntityInstance? {
        return getEntities(player, filter).minByOrNull { it.getLocation().safeDistance(player.location) }
    }

    override fun getNearestEntity(location: Location, filter: Predicate<EntityInstance>): EntityInstance? {
        return getEntities(null, filter).minByOrNull { it.getLocation().safeDistance(location) }
    }

    override fun getNearestEntityFromId(id: String, player: Player): EntityInstance? {
        return getNearestEntity(player) { it.id == id }
    }

    override fun getNearestEntityFromId(id: String, location: Location): EntityInstance? {
        return getNearestEntity(location) { it.id == id }
    }

    override fun getNearestEntityFromIdOrUniqueId(id: String, player: Player): EntityInstance? {
        return getNearestEntity(player) { it.id == id || it.uniqueId == id }
    }

    override fun getNearestEntityFromIdOrUniqueId(id: String, location: Location): EntityInstance? {
        return getNearestEntity(location) { it.id == id || it.uniqueId == id }
    }

    companion object {

        val clientEntityMap = ConcurrentHashMap<String, MutableMap<Int, ClientEntity>>()

        @SubscribeEvent
        fun onQuit(e: PlayerQuitEvent) {
            clientEntityMap.remove(e.player.name)
        }
    }
}