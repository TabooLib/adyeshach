package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.entity.manager.PlayerManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.IsolatedPlayerManager
 *
 * @author 坏黑
 * @since 2022/6/28 15:10
 */
open class IsolatedPlayerManager(owner: Player) : BaseManager(), PlayerManager {

    override val uniqueId: UUID = owner.uniqueId

    override var owner = owner
        get() {
            // 2023/5/31 玩家对象因未知原因可能失效，重新获取
            if (!field.isOnline) {
                Bukkit.getPlayer(uniqueId)?.let { field = it }
            }
            return field
        }

    override fun isValid(): Boolean {
        return Bukkit.getPlayer(uniqueId) != null
    }

    override fun isPublic(): Boolean {
        return false
    }

    override fun getPlayers(): List<Player> {
        return listOf(owner)
    }
}