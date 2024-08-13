package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachAPI
import ink.ptms.adyeshach.core.AdyeshachEntityFinder
import ink.ptms.adyeshach.core.entity.ClientEntity
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.core.util.safeDistance
import ink.ptms.adyeshach.core.util.safeDistanceIgnoreY
import ink.ptms.adyeshach.impl.storage.EntityStorage
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.event.SubscribeEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachEntityFinder
 *
 * @author 坏黑
 * @since 2022/6/19 00:15
 */
class DefaultAdyeshachEntityFinder : AdyeshachEntityFinder {

    val api: AdyeshachAPI
        get() = Adyeshach.api()

    override fun getEntity(player: Player?, match: Predicate<EntityInstance>): EntityInstance? {
        api.getPublicEntityManager(ManagerType.PERSISTENT).getEntity(match)?.let { return it }
        api.getPublicEntityManager(ManagerType.TEMPORARY).getEntity(match)?.let { return it }
        if (player != null) {
            if (EntityStorage.isEnabled()) {
                api.getPrivateEntityManager(player, ManagerType.PERSISTENT).getEntity(match)?.let { return it }
            }
            api.getPrivateEntityManager(player, ManagerType.TEMPORARY).getEntity(match)?.let { return it }
        }
        return null
    }

    override fun getEntities(player: Player?, filter: Predicate<EntityInstance>): List<EntityInstance> {
        val entity = LinkedList<EntityInstance>()
        entity.addAll(api.getPublicEntityManager(ManagerType.PERSISTENT).getEntities(filter))
        entity.addAll(api.getPublicEntityManager(ManagerType.TEMPORARY).getEntities(filter))
        if (player != null) {
            if (EntityStorage.isEnabled()) {
                entity.addAll(api.getPrivateEntityManager(player, ManagerType.PERSISTENT).getEntities(filter))
            }
            entity.addAll(api.getPrivateEntityManager(player, ManagerType.TEMPORARY).getEntities(filter))
        }
        return entity
    }

    override fun getVisibleEntities(player: Player, filter: Predicate<EntityInstance>): List<EntityInstance> {
        return getEntities(player) { it.isViewer(player) && it.getLocation().safeDistanceIgnoreY(player.location) <= it.visibleDistance && filter.test(it) }
    }

    override fun getEntitiesFromId(id: String, player: Player?): List<EntityInstance> {
        return getEntities(player) { it.id == id }
    }

    override fun getEntitiesFromIdOrUniqueId(id: String, player: Player?): List<EntityInstance> {
        return getEntities(player) { it.id == id || it.uniqueId == id }
    }

    override fun getEntityFromEntityId(id: Int, player: Player?): EntityInstance? {
        return getEntity(player) { it.index == id }
    }

    override fun getEntityFromUniqueId(id: String, player: Player?): EntityInstance? {
        return getEntity(player) { it.uniqueId == id }
    }

    override fun getEntityFromClientEntityId(id: Int, player: Player): EntityInstance? {
        return clientEntityMap[player.name]?.values?.firstOrNull { it.entityId == id }?.entity
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

        @Awake(LifeCycle.CONST)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachEntityFinder>(DefaultAdyeshachEntityFinder())
        }
    }
}