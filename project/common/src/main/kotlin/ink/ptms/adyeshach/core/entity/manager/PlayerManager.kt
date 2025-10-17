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

    /** 持有者 */
    var owner: Player

    val uniqueId: UUID
}