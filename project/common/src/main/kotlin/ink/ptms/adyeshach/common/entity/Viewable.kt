package ink.ptms.adyeshach.common.entity

import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.Viewable
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
    var visibleDistance: Int

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
    fun addViewer(viewer: Player)

    /**
     * 移除观察者，在公开状态下这个选项无效
     */
    fun removeViewer(viewer: Player)

    /**
     * 清空观察者
     */
    fun clearViewer()

    /**
     * 是否拥有有效观察者
     */
    fun hasViewer(): Boolean

    /**
     * 是否为观察者
     */
    fun isViewer(viewer: Player): Boolean

    /**
     * 是否为真实观察者（在观察范围内）
     */
    fun isVisibleViewer(viewer: Player): Boolean

    /**
     * 玩家是否在观察范围内
     */
    fun isInVisibleDistance(player: Player): Boolean

    /**
     * 遍历所有有效观察者
     */
    fun forViewers(viewer: Consumer<Player>)
}