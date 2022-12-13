package ink.ptms.adyeshach.common.api

import ink.ptms.adyeshach.common.bukkit.data.GameProfile
import ink.ptms.adyeshach.common.bukkit.data.GameProfileAction
import org.bukkit.entity.Player
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.MinecraftEntityPlayerHandler
 *
 * @author 坏黑
 * @since 2022/6/15 18:37
 */
interface MinecraftEntityPlayerHandler {

    /**
     * 向玩家客户端中添加数据虚拟玩家信息
     *
     * @param player 数据包接收人
     * @param uuid 虚拟玩家 UUID
     * @param gameProfile 虚拟玩家 GameProfile
     */
    fun addPlayerInfo(player: Player, uuid: UUID, gameProfile: GameProfile)

    /**
     * 向玩家客户端中更新数据虚拟玩家信息
     *
     * @param player 数据包接收人
     * @param uuid 虚拟玩家 UUID
     * @param gameProfile 虚拟玩家 GameProfile
     * @param actions 更新类型
     */
    fun updatePlayerInfo(player: Player, uuid: UUID, gameProfile: GameProfile, actions: List<GameProfileAction>)

    /**
     * 移除玩家客户端重的虚拟玩家信息
     *
     * @param player 数据包接收人
     * @param uuid 虚拟玩家 UUID
     */
    fun removePlayerInfo(player: Player, uuid: UUID)
}