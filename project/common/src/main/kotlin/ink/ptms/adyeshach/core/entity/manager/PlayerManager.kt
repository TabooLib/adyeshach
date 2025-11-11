package ink.ptms.adyeshach.core.entity.manager

import org.bukkit.entity.Player
import java.util.UUID

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.manager.PlayerManager
 *
 * @author 坏黑
 * @since 2023/1/5 17:38
 */
interface PlayerManager {

    /**
     * 持有者
     * 每次获取都会进行 isValid 检查，玩家必定有效
     */
    val owner: Player

    /**
     * 持有者唯一标识
     */
    val uniqueId: UUID
}