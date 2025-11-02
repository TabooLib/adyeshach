package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.entity.manager.PlayerManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.function.throttle
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.DefaultPlayerManager
 *
 * @author 坏黑
 * @since 2022/6/28 15:10
 */
open class DefaultPlayerManager(owner: Player) : DefaultManager(), PlayerManager {

    override val uniqueId: UUID = owner.uniqueId

    override var owner = owner
        get() {
            // 2023/5/31 玩家对象因未知原因可能失效，重新获取
            if (!field.isOnline) {
                Bukkit.getPlayer(uniqueId)?.let { field = it }
            }
            return field
        }

    private var isValid = true

    val updateValid = throttle(50) {
        isValid = Bukkit.getPlayer(uniqueId) != null
    }

    override fun isValid(): Boolean {
        updateValid()
        return isValid
    }

    override fun isPublic(): Boolean {
        return false
    }

    override fun getPlayers(): List<Player> {
        return listOf(owner)
    }
}