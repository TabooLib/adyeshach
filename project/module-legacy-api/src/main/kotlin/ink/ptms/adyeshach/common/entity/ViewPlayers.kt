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
        get() = ConcurrentSet<String>().also { it.addAll(entityInstance.v2.viewPlayers.viewers) }

    val visible: ConcurrentSet<String>
        get() = ConcurrentSet<String>().also { it.addAll(entityInstance.v2.viewPlayers.visible) }

    fun getPlayers(): List<Player> {
        return entityInstance.v2.viewPlayers.getPlayers()
    }

    fun getPlayersInViewDistance(): List<Player> {
        return entityInstance.v2.viewPlayers.getPlayersInViewDistance()
    }

    fun getViewPlayers(): List<Player> {
        return entityInstance.v2.viewPlayers.getViewPlayers()
    }

    fun getOutsidePlayers(): List<Player> {
        return entityInstance.v2.viewPlayers.getOutsidePlayers()
    }

    fun hasVisiblePlayer(): Boolean {
        return entityInstance.v2.viewPlayers.hasVisiblePlayer()
    }

    override fun toString(): String {
        return "ViewPlayers(viewers=$viewers, visible=$visible)"
    }
}