package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.entity.manager.PlayerManager
import org.bukkit.entity.Player
import taboolib.platform.util.PlayerSessionClosable
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.DefaultPlayerManager
 *
 * @author 坏黑
 * @since 2022/6/28 15:10
 */
open class DefaultPlayerManager(override val owner: Player) : DefaultManager(), PlayerManager, PlayerSessionClosable {

    override val uniqueId: UUID = owner.uniqueId

    private var isValid = true

    override fun isValid(): Boolean {
        return isValid
    }

    override fun isPublic(): Boolean {
        return false
    }

    override fun getPlayers(): List<Player> {
        return listOf(owner)
    }

    override fun onSessionRemove(uuid: UUID) {
        isValid = false
    }
}