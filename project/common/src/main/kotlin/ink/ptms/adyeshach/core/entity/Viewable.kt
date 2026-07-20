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
     * 伴生实体无法直接操作观察者
     */
    fun addViewer(viewer: Player) {
        // 伴生实体禁止直接操作观察者
        if (this is Companionable && this.isCompanion()) return
        viewPlayers.viewers.add(viewer.name)
        // 同步到伴生实体
        // 伴生 visible 由宿主 visible 生命周期同步，禁止在此直接改写
        if (!visible(viewer, true)) {
            // 宿主未实际 spawn 时单独传播 ACL，避免成功 spawn 已遍历整棵伴生树后再次重复遍历。
            syncCompanionViewer(viewer, true)
        }
    }

    /**
     * 移除观察者，在公开状态下这个选项无效
     * 伴生实体无法直接操作观察者
     */
    fun removeViewer(viewer: Player) {
        // 伴生实体禁止直接操作观察者
        if (this is Companionable && this.isCompanion()) return
        viewPlayers.viewers.remove(viewer.name)
        // 同步到伴生实体
        // 伴生 visible 由宿主 visible 生命周期同步，禁止在此直接改写
        if (!visible(viewer, false)) {
            // 宿主未实际 destroy 时仍需单独释放 ACL；成功 destroy 已沿伴生树完成清理。
            syncCompanionViewer(viewer, false)
        }
    }

    /**
     * 沿伴生树同步观察者授权，遇到“公共宿主 -> 私有伴生”时截断整棵子树
     *
     * @param viewer 观察者
     * @param viewing 是否加入观察者授权
     */
    fun syncCompanionViewer(viewer: Player, viewing: Boolean) {
        if (this !is EntityInstance) {
            return
        }
        fun sync(host: EntityInstance) {
            host.getCompanions().forEach { companion ->
                if (!companion.isPublic() && host.isPublic()) {
                    return@forEach
                }
                if (viewing) {
                    companion.viewPlayers.viewers.add(viewer.name)
                } else {
                    companion.viewPlayers.viewers.remove(viewer.name)
                }
                sync(companion)
            }
        }
        sync(this)
    }

    /**
     * 释放观察者缓存，不向玩家发送 destroy 包
     * 玩家退出时连接已断开，只需要清理服务端可见性状态，避免对离线连接执行无意义发包。
     */
    fun releaseViewerCache(viewer: Player) {
        // 伴生实体禁止直接操作观察者
        if (this is Companionable && this.isCompanion()) return
        viewPlayers.viewers.remove(viewer.name)
        viewPlayers.visible.remove(viewer.name)
        // 同步到伴生实体
        if (this is EntityInstance) {
            fun release(host: EntityInstance) {
                host.getCompanions().forEach {
                    it.viewPlayers.viewers.remove(viewer.name)
                    it.viewPlayers.visible.remove(viewer.name)
                    release(it)
                }
            }
            // 退出清理不受 ACL 截断，确保历史残留的深层状态也被释放。
            release(this)
        }
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
