package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.entity.ViewPlayers
import ink.ptms.adyeshach.impl.manager.DefaultManagerHandler.playersInGameTick
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultViewPlayers
 *
 * @author 坏黑
 * @since 2022/6/28 01:54
 */
class DefaultViewPlayers(val entityInstance: DefaultEntityInstance) : ViewPlayers {

    // 回调函数列表
    private val viewerAddedHandlers = mutableListOf<Consumer<String>>()
    private val viewerRemovedHandlers = mutableListOf<Consumer<String>>()
    private val visibleAddedHandlers = mutableListOf<Consumer<String>>()
    private val visibleRemovedHandlers = mutableListOf<Consumer<String>>()

    // 优化：使用 ConcurrentHashMap.newKeySet() 替代 ConcurrentSkipListSet
    // O(1) 查找性能 vs O(log n)
    val viewersDelegate = ConcurrentHashMap.newKeySet<String>()

    override val viewers: MutableSet<String> = object : MutableSet<String> by viewersDelegate {
        // region
        override fun add(element: String): Boolean {
            val result = viewersDelegate.add(element)
            if (result) {
                viewerAddedHandlers.forEach { it.accept(element) }
            }
            return result
        }

        override fun addAll(elements: Collection<String>): Boolean {
            val added = elements.filter { viewersDelegate.add(it) }
            if (added.isNotEmpty()) {
                added.forEach { element -> viewerAddedHandlers.forEach { it.accept(element) } }
            }
            return added.isNotEmpty()
        }

        override fun remove(element: String): Boolean {
            val result = viewersDelegate.remove(element)
            if (result) {
                viewerRemovedHandlers.forEach { it.accept(element) }
            }
            return result
        }

        override fun removeAll(elements: Collection<String>): Boolean {
            val removed = elements.filter { viewersDelegate.remove(it) }
            if (removed.isNotEmpty()) {
                removed.forEach { element -> viewerRemovedHandlers.forEach { it.accept(element) } }
            }
            return removed.isNotEmpty()
        }

        override fun retainAll(elements: Collection<String>): Boolean {
            val toRemove = viewersDelegate.filter { it !in elements }
            val result = viewersDelegate.retainAll(elements)
            if (result && toRemove.isNotEmpty()) {
                toRemove.forEach { element -> viewerRemovedHandlers.forEach { it.accept(element) } }
            }
            return result
        }

        override fun clear() {
            val removed = viewersDelegate.toList()
            viewersDelegate.clear()
            if (removed.isNotEmpty()) {
                removed.forEach { element -> viewerRemovedHandlers.forEach { it.accept(element) } }
            }
        }
        // endregion
    }

    // 优化：维护 hasVisiblePlayer 状态，避免每次调用 isEmpty()
    val hasVisiblePlayerState = AtomicBoolean(false)

    val visibleDelegate = ConcurrentHashMap.newKeySet<String>()

    override val visible: MutableSet<String> = object : MutableSet<String> by visibleDelegate {
        // region
        override fun add(element: String): Boolean {
            val result = visibleDelegate.add(element)
            if (result) {
                hasVisiblePlayerState.set(true)
                visibleAddedHandlers.forEach { it.accept(element) }
            }
            return result
        }

        override fun addAll(elements: Collection<String>): Boolean {
            val added = elements.filter { visibleDelegate.add(it) }
            if (added.isNotEmpty()) {
                hasVisiblePlayerState.set(true)
                added.forEach { element -> visibleAddedHandlers.forEach { it.accept(element) } }
            }
            return added.isNotEmpty()
        }

        override fun remove(element: String): Boolean {
            val result = visibleDelegate.remove(element)
            if (result) {
                if (visibleDelegate.isEmpty()) {
                    hasVisiblePlayerState.set(false)
                }
                visibleRemovedHandlers.forEach { it.accept(element) }
            }
            return result
        }

        override fun removeAll(elements: Collection<String>): Boolean {
            val removed = elements.filter { visibleDelegate.remove(it) }
            if (removed.isNotEmpty()) {
                if (visibleDelegate.isEmpty()) {
                    hasVisiblePlayerState.set(false)
                }
                removed.forEach { element -> visibleRemovedHandlers.forEach { it.accept(element) } }
            }
            return removed.isNotEmpty()
        }

        override fun retainAll(elements: Collection<String>): Boolean {
            val toRemove = visibleDelegate.filter { it !in elements }
            val result = visibleDelegate.retainAll(elements)
            if (result) {
                if (visibleDelegate.isEmpty()) {
                    hasVisiblePlayerState.set(false)
                }
                if (toRemove.isNotEmpty()) {
                    toRemove.forEach { element -> visibleRemovedHandlers.forEach { it.accept(element) } }
                }
            }
            return result
        }

        override fun clear() {
            val removed = visibleDelegate.toList()
            visibleDelegate.clear()
            hasVisiblePlayerState.set(false)
            if (removed.isNotEmpty()) {
                removed.forEach { element -> visibleRemovedHandlers.forEach { it.accept(element) } }
            }
        }
        // endregion
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

    override fun hasViewer(): Boolean {
        return viewersDelegate.isNotEmpty()
    }

    override fun onViewerAdded(handler: Consumer<String>) {
        viewerAddedHandlers.add(handler)
    }

    override fun onViewerRemoved(handler: Consumer<String>) {
        viewerRemovedHandlers.add(handler)
    }

    override fun onVisibleAdded(handler: Consumer<String>) {
        visibleAddedHandlers.add(handler)
    }

    override fun onVisibleRemoved(handler: Consumer<String>) {
        visibleRemovedHandlers.add(handler)
    }

    override fun toString(): String {
        return "DefaultViewPlayers(viewers=$viewers, visible=$visible)"
    }
}