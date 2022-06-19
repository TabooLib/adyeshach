package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.entity.ViewPlayers
import ink.ptms.adyeshach.common.entity.Viewable
import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultViewable
 *
 * @author 坏黑
 * @since 2022/6/19 21:59
 */
interface DefaultViewable : Viewable {

    override val viewPlayers: ViewPlayers
        get() = TODO("Not yet implemented")

    override var visibleDistance: Int
        get() = TODO("Not yet implemented")
        set(value) {}

    override var visibleAfterLoaded: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun addViewer(viewer: Player) {
        TODO("Not yet implemented")
    }

    override fun removeViewer(viewer: Player) {
        TODO("Not yet implemented")
    }

    override fun clearViewer() {
        TODO("Not yet implemented")
    }

    override fun hasViewer(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isViewer(viewer: Player): Boolean {
        TODO("Not yet implemented")
    }

    override fun isVisibleViewer(viewer: Player): Boolean {
        TODO("Not yet implemented")
    }

    override fun isInVisibleDistance(player: Player): Boolean {
        TODO("Not yet implemented")
    }

    override fun forViewers(viewer: Consumer<Player>) {
        TODO("Not yet implemented")
    }
}