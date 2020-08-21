package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.event.AdyeshachEntityDestroyEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntitySpawnEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.ai.Pathfinder
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.path.PathFinderProxy
import ink.ptms.adyeshach.common.path.PathType
import ink.ptms.adyeshach.common.util.Indexs
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 12:51
 */
abstract class EntityInstance(entityTypes: EntityTypes) : EntityBase(entityTypes) {

    /**
     * 实体序号
     */
    val index by lazy { Indexs.nextIndex() }

    /**
     * 管理工具
     */
    var manager: Manager? = null
        set(value) {
            if (field != null) {
                throw RuntimeException("Entity Manager has been initialized.")
            }
            field = value
        }

    /**
     * 玩家管理
     */
    val viewPlayers = ViewPlayers()

    /**
     * 实体逻辑
     */
    val pathfinder = ArrayList<Pathfinder>()

    /**
     * ai
     */

    init {
        registerMetaByteMask(0, "onFire", 0x01)
        registerMetaByteMask(0, "isCrouched", 0x02)
        registerMetaByteMask(0, "unUsedRiding", 0x04)
        registerMetaByteMask(0, "isSprinting", 0x08)
        registerMetaByteMask(0, "isSwimming", 0x010)
        registerMetaByteMask(0, "isInvisible", 0x20)
        registerMetaByteMask(0, "isGlowing", 0x40)
        registerMetaByteMask(0, "isFlyingElytra", 0x80.toByte())
        registerMeta(2, "customName", TextComponent(""))
        registerMeta(3, "isCustomNameVisible", false)
        registerMeta(at(11000 to 5, 10900 to -1), "noGravity", false)
    }

    /**
     * 内部调用方法，用于第三方事件处理
     */
    protected fun spawn(viewer: Player, spawn: () -> (Unit)) {
        if (AdyeshachEntityVisibleEvent(this, viewer, true).call().nonCancelled()) {
            spawn.invoke()
            updateMetadata()
            setHeadRotation(position.yaw, position.pitch)
        }
    }

    /**
     * 内部调用方法
     */
    protected fun destroy(viewer: Player, destroy: () -> (Unit)) {
        if (AdyeshachEntityVisibleEvent(this, viewer, false).call().nonCancelled()) {
            destroy.invoke()
        }
    }

    fun forViewers(viewer: (Player) -> (Unit)) {
        viewPlayers.getViewers().forEach { viewer.invoke(it) }
    }

    /**
     * 是否公开取决于 manager 是否为 ManagerPublic 即由谁管理，而非其他参数
     */
    fun isPublic(): Boolean {
        return manager?.isPublic() == true
    }

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
     * 生成实体，会覆盖相同 index 的实体。
     */
    fun spawn(location: Location) {
        position = EntityPosition.fromLocation(location)
        forViewers {
            visible(it, true)
        }
        AdyeshachEntitySpawnEvent(this).call()
    }

    /**
     * 重新生成实体
     */
    fun respawn() {
        spawn(position.toLocation())
    }

    /**
     * 销毁实体，从玩家视野中移除该实体
     */
    fun destroy() {
        forViewers {
            visible(it, false)
        }
        AdyeshachEntityDestroyEvent(this).call()
    }

    /**
     * 删除实体，从管理器中移除，不再接受托管，但不会销毁实体
     */
    fun remove() {
        if (manager != null) {
            manager!!.remove(this)
            AdyeshachEntityRemoveEvent(this).call()
        }
    }

    /**
     * 删除实体，并销毁
     */
    fun delete() {
        destroy()
        remove()
    }

    /**
     * 切换可视状态
     */
    abstract fun visible(viewer: Player, visible: Boolean)

    /**
     * 修改实体位置
     */
    fun teleport(location: Location) {
        position = EntityPosition.fromLocation(location)
        forViewers {
            NMS.INSTANCE.teleportEntity(it, index, location)
        }
    }

    fun teleport(x: Double, y: Double, z: Double) {
        teleport(position.toLocation().run {
            this.x = x
            this.y = y
            this.z = z
            this
        })
    }

    /**
     * 修改实体视角
     */
    fun setHeadRotation(yaw: Float, pitch: Float) {
        position = position.run {
            this.yaw = yaw
            this.pitch = pitch
            this
        }
        forViewers {
            NMS.INSTANCE.setHeadRotation(it, index, yaw, pitch)
        }
    }

    /**
     * 使实体看向某个坐标
     */
    fun controllerLook(location: Location) {
        if (pathfinder.any { it is GeneralSmoothLook }) {
            val look = pathfinder.first { it is GeneralSmoothLook } as GeneralSmoothLook
            position.toLocation().add(0.0, entityType.entitySize.height * 0.9, 0.0).also { entityLocation ->
                entityLocation.direction = location.clone().subtract(entityLocation).toVector()
                look.yaw = entityLocation.yaw
                look.pitch = entityLocation.pitch
                look.isLooking = true
            }
        } else {
            position.toLocation().add(0.0, entityType.entitySize.height * 0.9, 0.0).also { entityLocation ->
                entityLocation.direction = location.clone().subtract(entityLocation).toVector()
                setHeadRotation(entityLocation.yaw, entityLocation.pitch)
            }
        }
    }

    /**
     * 使实体移动到某个坐标
     */
    fun controllerMove(location: Location, pathType: PathType = PathType.WALK_2, speed: Double = 0.2) {
        if (pathType.supportVersion > version) {
            throw RuntimeException("PathType \"$pathType\" not supported this minecraft version.")
        }
        if (pathType == PathType.FLY) {
            if (pathfinder.none { it is GeneralMove }) {
                throw RuntimeException("Entity flying movement requires GeneralMove.")
            }
        } else {
            if (pathfinder.none { it is GeneralMove } || pathfinder.none { it is GeneralGravity }) {
                throw RuntimeException("Entity walking movement requires GeneralMove and GeneralGravity.")
            }
        }
        val move = pathfinder.first { it is GeneralMove } as GeneralMove
        PathFinderProxy.request(position.toLocation(), location, pathType) {
            move.speed = speed
            move.pathType = pathType
            move.pathResult = it
        }
    }

    fun isFired(): Boolean {
        return getMetadata("onFire")
    }

    fun isSneaking(): Boolean {
        return getMetadata("isCrouched")
    }

    fun isSprinting(): Boolean {
        return getMetadata("isSprinting")
    }

    fun isSwimming(): Boolean {
        return getMetadata("isSwimming")
    }

    fun isInvisible(): Boolean {
        return getMetadata("isInvisible")
    }

    fun isGlowing(): Boolean {
        return getMetadata("isGlowing")
    }

    fun isFlyingElytra(): Boolean {
        return getMetadata("isFlyingElytra")
    }

    fun isNoGravity(): Boolean {
        return getMetadata("noGravity")
    }

    fun setFired(onFire: Boolean) {
        setMetadata("onFire", onFire)
    }

    fun setSneaking(sneaking: Boolean) {
        setMetadata("sneaking", sneaking)
    }

    fun setSprinting(sprinting: Boolean) {
        setMetadata("isSprinting", sprinting)
    }

    fun setSwimming(swimming: Boolean) {
        setMetadata("isSwimming", swimming)
    }

    fun setInvisible(invisible: Boolean) {
        setMetadata("isInvisible", invisible)
    }

    fun setGlowing(glowing: Boolean) {
        setMetadata("isGlowing", glowing)
    }

    fun setFlyingElytra(flyingElytra: Boolean) {
        setMetadata("isFlyingElytra", flyingElytra)
    }

    fun setNoGravity(noGravity: Boolean) {
        setMetadata("noGravity", noGravity)
    }

    fun setCustomNameVisible(value: Boolean) {
        setMetadata("isCustomNameVisible", value)
    }

    fun isCustomNameVisible(): Boolean {
        return getMetadata("isCustomNameVisible")
    }

    fun setCustomName(value: String) {
        setMetadata("customName", value)
    }

    fun getCustomName(): String {
        return getMetadata("customName")
    }

    /**
     * 实体计算（async）
     */
    fun onTick() {
        // 确保客户端显示实体正常
        if (viewPlayers.visibleLock.next()) {
            // 复活
            viewPlayers.getOutsider().forEach { player ->
                if (player.world.name == position.world.name && player.location.distance(position.toLocation()) < 127) {
                    viewPlayers.visible.add(player.name)
                    visible(player, true)
                }
            }
            // 销毁
            viewPlayers.getViewers().forEach { player ->
                if (player.world.name != position.world.name || player.location.distance(position.toLocation()) > 127) {
                    viewPlayers.visible.remove(player.name)
                    visible(player, false)
                }
            }
        }
        // 实体逻辑处理
        pathfinder.filter { it.shouldExecute() }.forEach { it.onTick() }
    }
}