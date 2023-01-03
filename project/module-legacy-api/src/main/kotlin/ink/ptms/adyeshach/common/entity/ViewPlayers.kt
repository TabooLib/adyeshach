package ink.ptms.adyeshach.common.entity

import io.netty.util.internal.ConcurrentSet
import org.bukkit.entity.Player
import taboolib.common5.Baffle

/**
 * @author sky
 * @since 2020-08-14 21:57
 */
@Deprecated("Outdated but usable")
open class ViewPlayers(val entityInstance: EntityInstance) {

    val viewers: ConcurrentSet<String>
        get() = entityInstance.viewPlayers.viewers

    val visible: ConcurrentSet<String>
        get() = entityInstance.viewPlayers.visible

    val visibleRefreshLocker: Baffle
        get() = entityInstance.viewPlayers.visibleRefreshLocker

    fun getPlayers(): List<Player> {
        return entityInstance.viewPlayers.getPlayers()
    }

    fun getPlayersInViewDistance(): List<Player> {
        return entityInstance.viewPlayers.getPlayersInViewDistance()
    }

    fun getViewPlayers(): List<Player> {
        return entityInstance.viewPlayers.getViewPlayers()
    }

    fun getOutsidePlayers(): List<Player> {
        return entityInstance.viewPlayers.getOutsidePlayers()
    }

    fun hasVisiblePlayer(): Boolean {
        return entityInstance.viewPlayers.hasVisiblePlayer()
    }

    override fun toString(): String {
        return "ViewPlayers(viewers=$viewers, visible=$visible)"
    }
}