package ink.ptms.adyeshach.core.entity

import org.bukkit.entity.Player
import taboolib.platform.util.onlinePlayers
import java.util.function.Consumer

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.Viewable
 *
 * @author 坏黑
 * @since 2022/6/15 23:48
 */
interface Viewable {

    /**
     * 观察者容器
     */
    val viewPlayers: ViewPlayers

    /**
     * 可视距离，离开该距离后将回收实体
     */
    var visibleDistance: Double

    /**
     * 加载完成后自动显示，关闭后只能通过 API 控制玩家是否可见
     *
     * 仅限公共单位
     */
    var visibleAfterLoaded: Boolean

    /**
     * 切换可视状态
     */
    fun visible(viewer: Player, visible: Boolean): Boolean

    /**
     * 添加观察者，在公开状态下这个选项无效
     */
    fun addViewer(viewer: Player) {
        viewPlayers.viewers.add(viewer.name)
        viewPlayers.visible.add(viewer.name)
        visible(viewer, true)
    }

    /**
     * 移除观察者，在公开状态下这个选项无效
     */
    fun removeViewer(viewer: Player) {
        viewPlayers.viewers.remove(viewer.name)
        viewPlayers.visible.remove(viewer.name)
        visible(viewer, false)
    }

    /**
     * 清空观察者
     */
    fun clearViewer() {
        onlinePlayers.filter { it.name in viewPlayers.viewers }.forEach { removeViewer(it) }
    }

    /**
     * 是否拥有有效观察者
     */
    fun hasViewer(): Boolean {
        return viewPlayers.getViewPlayers().isNotEmpty()
    }

    /**
     * 是否为观察者
     */
    fun isViewer(viewer: Player): Boolean {
        return viewer.name in viewPlayers.viewers
    }

    /**
     * 是否为真实观察者（在观察范围内）
     */
    fun isVisibleViewer(viewer: Player): Boolean {
        return viewer.name in viewPlayers.viewers && viewer.name in viewPlayers.visible
    }

    /**
     * 玩家是否在观察范围内
     */
    fun isInVisibleDistance(player: Player): Boolean

    /**
     * 遍历所有有效观察者
     */
    fun forViewers(viewer: Consumer<Player>) {
        viewPlayers.getViewPlayers().forEach { viewer.accept(it) }
    }

    /**
     * 获取所有有效观察者
     */
    fun getVisiblePlayers(): List<Player>  {
        return viewPlayers.getViewPlayers()
    }

    /**
     * 检查可见性
     */
    fun checkVisible()
}