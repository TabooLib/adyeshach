package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachAPI
import ink.ptms.adyeshach.core.AdyeshachEntityFinder
import ink.ptms.adyeshach.core.entity.ClientEntity
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.core.util.safeDistance
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.platform.util.PlayerSessionMap
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

    /**
     * 玩家可见实体索引
     */
    private val playerVisibleEntitiesIndex = PlayerSessionMap<MutableSet<EntityInstance>>()

    /**
     * 添加实体到玩家的可见列表
     */
    override fun addVisibleEntity(player: Player, entity: EntityInstance) {
        playerVisibleEntitiesIndex.getOrCreate(player) { ConcurrentHashMap.newKeySet() }?.add(entity)
    }

    /**
     * 从玩家的可见列表中移除实体
     */
    override fun removeVisibleEntity(player: Player, entity: EntityInstance) {
        playerVisibleEntitiesIndex[player]?.remove(entity)
    }

    /**
     * 清理玩家的所有可见实体索引
     */
    override fun clearPlayerVisibleEntities(player: Player) {
        playerVisibleEntitiesIndex.remove(player)
    }

    /**
     * 从所有玩家的可见列表中移除实体（实体销毁时调用）
     */
    override fun removeEntityFromAllPlayers(entity: EntityInstance) {
        playerVisibleEntitiesIndex.values().forEach { it.remove(entity) }
    }

    override fun getEntity(player: Player?, match: Predicate<EntityInstance>): EntityInstance? {
        api.getPublicEntityManager(ManagerType.PERSISTENT).getEntity(match)?.let { return it }
        api.getPublicEntityManager(ManagerType.TEMPORARY).getEntity(match)?.let { return it }
        if (player != null) {
            api.getPrivateEntityManager(player, ManagerType.TEMPORARY).getEntity(match)?.let { return it }
        }
        return null
    }

    override fun getEntities(player: Player?, filter: Predicate<EntityInstance>): List<EntityInstance> {
        val entity = LinkedList<EntityInstance>()
        entity.addAll(api.getPublicEntityManager(ManagerType.PERSISTENT).getEntities(filter))
        entity.addAll(api.getPublicEntityManager(ManagerType.TEMPORARY).getEntities(filter))
        if (player != null) {
            entity.addAll(api.getPrivateEntityManager(player, ManagerType.TEMPORARY).getEntities(filter))
        }
        return entity
    }

    override fun getVisibleEntities(player: Player, filter: Predicate<EntityInstance>): List<EntityInstance> {
        // 优化：直接从索引中获取该玩家的可见实体,避免遍历所有管理器
        val visibleSet = playerVisibleEntitiesIndex[player]
        if (visibleSet == null || visibleSet.isEmpty()) {
            return emptyList()
        }
        val pLoc = player.location
        // 从索引中过滤，只需要检查距离和额外的过滤条件
        return visibleSet.filter { entity ->
            // 快速距离检查（使用平方距离避免开方运算）
            val loc = entity.position
            // 先检查世界是否相同
            if (loc.world.name != pLoc.world.name) {
                return@filter false
            }
            // 使用平方距离比较，避免 sqrt 计算
            val dx = loc.x - pLoc.x
            val dz = loc.z - pLoc.z
            val distanceSquared = dx * dx + dz * dz
            val visibleDistanceSquared = entity.visibleDistance * entity.visibleDistance
            distanceSquared <= visibleDistanceSquared && filter.test(entity)
        }
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
        return clientEntityMap[player]?.values?.firstOrNull { it.entityId == id }?.entity
    }

    override fun getEntityFromClientUniqueId(id: UUID, player: Player): EntityInstance? {
        return clientEntityMap[player]?.values?.firstOrNull { it.clientId == id }?.entity
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

        val clientEntityMap = PlayerSessionMap<MutableMap<Int, ClientEntity>>()

        @Awake(LifeCycle.CONST)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachEntityFinder>(DefaultAdyeshachEntityFinder())
        }
    }
}