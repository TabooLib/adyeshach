package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.entity.manager.PlayerManager
import org.bukkit.entity.Player
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.IsolatedPlayerManager
 *
 * @author 坏黑
 * @since 2022/6/28 15:10
 */
open class IsolatedPlayerManager(override val owner: Player) : BaseManager(), PlayerManager {

    override val uniqueId: UUID = owner.uniqueId

    override fun isValid(): Boolean {
        return owner.isValid
    }

    override fun isPublic(): Boolean {
        return false
    }

    override fun getPlayers(): List<Player> {
        return listOf(owner)
    }
}