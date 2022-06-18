package ink.ptms.adyeshach.internal

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.AdyeshachAPI
import ink.ptms.adyeshach.common.api.AdyeshachEntityFinder
import ink.ptms.adyeshach.common.entity.ClientEntity
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.safeDistance
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

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

    override fun getEntity(player: Player?, filter: Function<EntityInstance, Boolean>): EntityInstance? {
        val entity = getEntities(player, filter)
        return if (player != null) entity.minByOrNull { it.position.toLocation().safeDistance(player.location) } else entity.firstOrNull()
    }

    override fun getEntities(player: Player?, filter: Function<EntityInstance, Boolean>): List<EntityInstance> {
        val entity = ArrayList<EntityInstance>()
        entity.addAll(api.getPublicEntityManager().getEntities().filter { filter.apply(it) })
        entity.addAll(api.getPublicEntityManager(true).getEntities().filter { filter.apply(it) })
        if (player != null) {
            entity.addAll(api.getPrivateEntityManager(player).getEntities().filter { filter.apply(it) })
            entity.addAll(api.getPrivateEntityManager(player, true).getEntities().filter { filter.apply(it) })
        }
        return entity
    }

    override fun getEntityNearly(player: Player): EntityInstance? {
        return getEntity(player) { true }
    }

    override fun getEntityFromEntityId(id: Int, player: Player?): EntityInstance? {
        return getEntity(player) { it.index == id }
    }

    override fun getEntityFromId(id: String, player: Player?): EntityInstance? {
        return getEntity(player) { it.id == id }
    }

    override fun getEntityFromUniqueId(id: String, player: Player?): EntityInstance? {
        return getEntity(player) { it.uniqueId == id }
    }

    override fun getEntityFromUniqueIdOrId(id: String, player: Player?): EntityInstance? {
        return getEntity(player) { it.id == id || it.uniqueId == id }
    }

    override fun getEntityFromClientUniqueId(player: Player, uniqueId: UUID): EntityInstance? {
        return clientEntityMap[player.name]?.values?.firstOrNull { it.clientId == uniqueId }?.entity
    }

    companion object {

        val clientEntityMap = ConcurrentHashMap<String, MutableMap<Int, ClientEntity>>()
    }
}