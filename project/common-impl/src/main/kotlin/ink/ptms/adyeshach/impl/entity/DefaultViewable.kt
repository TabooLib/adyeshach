package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.core.entity.EntityBase
import ink.ptms.adyeshach.core.entity.Viewable
import ink.ptms.adyeshach.core.util.safeDistance
import ink.ptms.adyeshach.core.util.safeDistanceIgnoreY
import org.bukkit.entity.Player
import taboolib.platform.util.onlinePlayers
import java.util.function.Consumer

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultViewable
 *
 * @author 坏黑
 * @since 2022/6/19 21:59
 */
interface DefaultViewable : Viewable {

    override fun addViewer(viewer: Player) {
        viewPlayers.viewers.add(viewer.name)
        viewPlayers.visible.add(viewer.name)
        visible(viewer, true)
    }

    override fun removeViewer(viewer: Player) {
        viewPlayers.viewers.remove(viewer.name)
        viewPlayers.visible.remove(viewer.name)
        visible(viewer, false)
    }

    override fun clearViewer() {
        onlinePlayers.filter { it.name in viewPlayers.viewers }.forEach { removeViewer(it) }
    }

    override fun hasViewer(): Boolean {
        return viewPlayers.getViewPlayers().isNotEmpty()
    }

    override fun isViewer(viewer: Player): Boolean {
        return viewer.name in viewPlayers.viewers
    }

    override fun isVisibleViewer(viewer: Player): Boolean {
        return viewer.name in viewPlayers.viewers && viewer.name in viewPlayers.visible
    }

    override fun isInVisibleDistance(player: Player): Boolean {
        this as EntityBase
        return player.location.safeDistanceIgnoreY(getLocation()) < visibleDistance
    }

    override fun forViewers(viewer: Consumer<Player>) {
        viewPlayers.getViewPlayers().forEach { viewer.accept(it) }
    }

    override fun getVisiblePlayers(): List<Player> {
        return viewPlayers.getViewPlayers()
    }
}