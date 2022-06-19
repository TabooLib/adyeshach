package ink.ptms.adyeshach.common.api

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.AdyeshachEntityFinder
 *
 * @author 坏黑
 * @since 2022/6/16 16:39
 */
interface AdyeshachEntityFinder {

    /**
     * 通过过滤器获取单位
     */
    fun getEntities(player: Player? = null, filter: Function<EntityInstance, Boolean>): List<EntityInstance>

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
     * 通过玩家数据包中的 uuid 获取单位
     */
    fun getEntityFromClientUniqueId(id: UUID, player: Player): EntityInstance?

    /**
     * 通过过滤器获取玩家就近的单位（通过所有单位管理器）
     */
    fun getNearestEntity(player: Player, filter: Function<EntityInstance, Boolean>): EntityInstance?

    /**
     * 通过过滤器获取坐标就近的单位（通过公共单位管理器）
     */
    fun getNearestEntity(location: Location, filter: Function<EntityInstance, Boolean>): EntityInstance?

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
}