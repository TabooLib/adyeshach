package ink.ptms.adyeshach.common.bukkit.nms

import org.bukkit.entity.Player
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.bukkit.nms.MinecraftEntityPlayerHandler
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
     * @param name 虚拟玩家名称
     * @param ping 虚拟玩家延迟
     * @param texture 皮肤（因历史遗留问题，该数组所表示为：[0] = texture，[1] = signature）
     */
    fun addPlayerInfo(player: Player, uuid: UUID, name: String, ping: Int, texture: Array<String>)

    /**
     * 移除玩家客户端重的虚拟玩家信息
     *
     * @param player 数据包接收人
     * @param uuid 虚拟玩家 UUID
     */
    fun removePlayerInfo(player: Player, uuid: UUID)
}