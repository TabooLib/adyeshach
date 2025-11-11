package ink.ptms.adyeshach.core

import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachEntityFinder
 *
 * @author 坏黑
 * @since 2022/6/16 16:39
 */
interface AdyeshachEntityFinder {

    /**
     * 通过过滤器获取单位
     */
    fun getEntity(player: Player? = null, match: Predicate<EntityInstance> = Predicate { true }): EntityInstance?

    /**
     * 通过过滤器获取单位
     */
    fun getEntities(player: Player? = null, filter: Predicate<EntityInstance> = Predicate { true }): List<EntityInstance>

    /**
     * 通过过滤器获取所有玩家可见单位
     */
    fun getVisibleEntities(player: Player, filter: Predicate<EntityInstance> = Predicate { true }): List<EntityInstance>

    /**
     * 通过单位 id 获取单位
     */
    fun getEntitiesFromId(id: String, player: Player? = null): List<EntityInstance>

    /**
     * 通过单位 id 或 uuid 获取单位
     */
    fun getEntitiesFromIdOrUniqueId(id: String, player: Player? = null): List<EntityInstance>

    /**
     * 通过实体 id 获取单位
     */
    fun getEntityFromEntityId(id: Int, player: Player? = null): EntityInstance?

    /**
     * 通过 uuid 获取单位
     */
    fun getEntityFromUniqueId(id: String, player: Player? = null): EntityInstance?

    /**
     * 通过玩家数据包中的 id 获取单位
     */
    fun getEntityFromClientEntityId(id: Int, player: Player): EntityInstance?

    /**
     * 通过玩家数据包中的 uuid 获取单位
     */
    fun getEntityFromClientUniqueId(id: UUID, player: Player): EntityInstance?

    /**
     * 通过过滤器获取玩家就近的单位（通过所有单位管理器）
     */
    fun getNearestEntity(player: Player, filter: Predicate<EntityInstance> = Predicate { true }): EntityInstance?

    /**
     * 通过过滤器获取坐标就近的单位（通过公共单位管理器）
     */
    fun getNearestEntity(location: Location, filter: Predicate<EntityInstance> = Predicate { true }): EntityInstance?

    /**
     * 通过单位 id 获取单位
     */
    fun getNearestEntityFromId(id: String, player: Player): EntityInstance?

    /**
     * 通过单位 id 获取单位
     */
    fun getNearestEntityFromId(id: String, location: Location): EntityInstance?

    /**
     * 通过 uuid 或单位 id 获取单位
     */
    fun getNearestEntityFromIdOrUniqueId(id: String, player: Player): EntityInstance?

    /**
     * 通过 uuid 或单位 id 获取单位
     */
    fun getNearestEntityFromIdOrUniqueId(id: String, location: Location): EntityInstance?

    /**
     * 添加实体到玩家的可见列表
     */
    fun addVisibleEntity(player: Player, entity: EntityInstance)

    /**
     * 从玩家的可见列表中移除实体
     */
    fun removeVisibleEntity(player: Player, entity: EntityInstance)

    /**
     * 清理玩家的所有可见实体索引
     */
    fun clearPlayerVisibleEntities(player: Player)

    /**
     * 从所有玩家的可见列表中移除实体（实体销毁时调用）
     */
    fun removeEntityFromAllPlayers(entity: EntityInstance)
}