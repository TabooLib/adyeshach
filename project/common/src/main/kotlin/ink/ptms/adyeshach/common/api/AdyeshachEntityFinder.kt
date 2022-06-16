package ink.ptms.adyeshach.common.api

import ink.ptms.adyeshach.common.entity.EntityInstance
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
     * 获取一个单位
     *
     * @param filter 过滤
     */
    fun getEntity(player: Player? = null, filter: Function<EntityInstance, Boolean>): EntityInstance?

    /**
     * 获取多个单位
     *
     * @param filter 过滤
     */
    fun getEntities(player: Player? = null, filter: Function<EntityInstance, Boolean>): List<EntityInstance>

    /**
     * 获取玩家就近的单位（包含公共、私有单位管理器中的单位）
     */
    fun getEntityNearly(player: Player): EntityInstance?

    /**
     * 通过实体 id 获取单位
     */
    fun getEntityFromEntityId(id: Int, player: Player? = null): EntityInstance?

    /**
     * 通过单位 id 获取单位
     */
    fun getEntityFromId(id: String, player: Player? = null): EntityInstance?

    /**
     * 通过 uuid 获取单位
     */
    fun getEntityFromUniqueId(id: String, player: Player? = null): EntityInstance?

    /**
     * 通过 uuid 或单位 id 获取单位
     */
    fun getEntityFromUniqueIdOrId(id: String, player: Player? = null): EntityInstance?

    /**
     * 通过玩家数据包中的 uuid 获取单位
     */
    fun getEntityFromClientUniqueId(player: Player, uniqueId: UUID): EntityInstance?
}