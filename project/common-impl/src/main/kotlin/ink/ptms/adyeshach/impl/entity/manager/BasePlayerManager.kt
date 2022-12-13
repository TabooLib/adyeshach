package ink.ptms.adyeshach.impl.entity.manager

import org.bukkit.entity.Player

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.manager.DefaultPlayerIsolateManager
 *
 * @author 坏黑
 * @since 2022/6/28 15:10
 */
open class BasePlayerManager(val player: Player) : BaseManager() {

    override fun getPlayers(): List<Player> {
        return listOf(player)
    }
}