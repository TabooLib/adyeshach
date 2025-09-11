package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.entity.ViewPlayers
import ink.ptms.adyeshach.impl.manager.DefaultManagerHandler.playersInGameTick
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentSkipListSet
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultViewPlayers
 *
 * @author 坏黑
 * @since 2022/6/28 01:54
 */
class DefaultViewPlayers(val entityInstance: DefaultEntityInstance) : ViewPlayers {

    override val viewers = ConcurrentSkipListSet<String>()

    override val visible = ConcurrentSkipListSet<String>()

    override fun getPlayers(): List<Player> {
        return playersInGameTick.filter { it.name in viewers }
    }

    override fun getPlayersInViewDistance(): List<Player> {
        return playersInGameTick.filter { it.name in viewers && entityInstance.isInVisibleDistance(it) }
    }

    override fun getViewPlayers(): List<Player> {
        return playersInGameTick.filter { it.name in viewers && it.name in visible }
    }

    override fun getViewPlayers(cond: Function<Player, Boolean>): List<Player> {
        return playersInGameTick.filter { it.name in viewers && it.name in visible && cond.apply(it) }
    }

    override fun getOutsidePlayers(): List<Player> {
        return playersInGameTick.filter { it.name in viewers && it.name !in visible }
    }

    override fun getOutsidePlayers(cond: Function<Player, Boolean>): List<Player> {
        return playersInGameTick.filter { it.name in viewers && it.name !in visible && cond.apply(it) }
    }

    override fun hasVisiblePlayer(): Boolean {
        return visible.isNotEmpty()
    }

    override fun toString(): String {
        return "DefaultViewPlayers(viewers=$viewers, visible=$visible)"
    }
}