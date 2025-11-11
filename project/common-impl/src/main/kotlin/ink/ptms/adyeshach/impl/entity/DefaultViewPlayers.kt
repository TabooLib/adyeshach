package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.entity.ViewPlayers
import ink.ptms.adyeshach.impl.manager.DefaultManagerHandler.playersInGameTick
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultViewPlayers
 *
 * @author 坏黑
 * @since 2022/6/28 01:54
 */
class DefaultViewPlayers(val entityInstance: DefaultEntityInstance) : ViewPlayers {

    // 优化：使用 ConcurrentHashMap.newKeySet() 替代 ConcurrentSkipListSet
    // O(1) 查找性能 vs O(log n)
    override val viewers = ConcurrentHashMap.newKeySet<String>()

    // 优化：维护 hasVisiblePlayer 状态，避免每次调用 isEmpty()
    val hasVisiblePlayerState = AtomicBoolean(false)

    val visibleDelegate = ConcurrentHashMap.newKeySet<String>()

    override val visible: MutableSet<String> = object : MutableSet<String> by visibleDelegate {

        override fun add(element: String): Boolean {
            val result = visibleDelegate.add(element)
            if (result) {
                hasVisiblePlayerState.set(true)
            }
            return result
        }

        override fun addAll(elements: Collection<String>): Boolean {
            val result = visibleDelegate.addAll(elements)
            if (result && visibleDelegate.isNotEmpty()) {
                hasVisiblePlayerState.set(true)
            }
            return result
        }

        override fun remove(element: String): Boolean {
            val result = visibleDelegate.remove(element)
            if (result && visibleDelegate.isEmpty()) {
                hasVisiblePlayerState.set(false)
            }
            return result
        }

        override fun removeAll(elements: Collection<String>): Boolean {
            val result = visibleDelegate.removeAll(elements)
            if (result && visibleDelegate.isEmpty()) {
                hasVisiblePlayerState.set(false)
            }
            return result
        }

        override fun retainAll(elements: Collection<String>): Boolean {
            val result = visibleDelegate.retainAll(elements)
            if (result && visibleDelegate.isEmpty()) {
                hasVisiblePlayerState.set(false)
            }
            return result
        }

        override fun clear() {
            visibleDelegate.clear()
            hasVisiblePlayerState.set(false)
        }
    }

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
        return hasVisiblePlayerState.get()
    }

    override fun toString(): String {
        return "DefaultViewPlayers(viewers=$viewers, visible=$visible)"
    }
}