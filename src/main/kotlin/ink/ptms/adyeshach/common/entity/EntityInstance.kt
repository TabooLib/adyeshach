package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.Settings
import ink.ptms.adyeshach.api.event.*
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.entity.manager.ManagerPrivateTemp
import ink.ptms.adyeshach.common.entity.manager.ManagerPublicTemp
import ink.ptms.adyeshach.common.entity.path.PathFinderProxy
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.path.ResultNavigation
import ink.ptms.adyeshach.common.util.Indexs
import io.izzel.taboolib.internal.gson.JsonParser
import io.izzel.taboolib.internal.gson.annotations.Expose
import io.izzel.taboolib.util.chat.TextComponent
import io.netty.util.internal.ConcurrentSet
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

/**
 * @Author sky
 * @Since 2020-08-04 12:51
 */
abstract class EntityInstance(entityTypes: EntityTypes) : EntityBase(entityTypes) {

    @Expose
    private var passengers = ConcurrentSet<String>()
    private val controller = CopyOnWriteArrayList<Controller>()

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
        registerMeta(at(11000 to 5), "noGravity", false)
        if (version >= 11400) {
            registerMeta(at(11400 to 6), "pose", BukkitPose.STANDING)
            registerEditor("entityPose")
                    .from(Editors.enums(BukkitPose::class) { _, entity, meta, _, e -> "/adyeshachapi edit pose ${entity.uniqueId} ${meta.key} $e" })
                    .reset { entity, _ ->
                        entity.setPose(BukkitPose.STANDING)
                    }
                    .display { _, entity, _ ->
                        entity.getPose().name
                    }
        }
    }

    /**
     * 切换可视状态
     */
    abstract fun visible(viewer: Player, visible: Boolean)

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
     * 是否为临时实体，即非持久化储存
     */
    fun isTemporary(): Boolean {
        return manager is ManagerPublicTemp || manager is ManagerPrivateTemp
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
     * 清空观察者
     */
    fun clearViewer() {
        Bukkit.getOnlinePlayers().filter { it.name in viewPlayers.viewers }.forEach {
            removeViewer(it)
        }
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
     * 修改实体位置
     */
    fun teleport(location: Location) {
        val event = AdyeshachEntityTeleportEvent(this, location).call()
        if (event.isCancelled) {
            return
        }
        position = EntityPosition.fromLocation(event.location)
        forViewers {
            NMS.INSTANCE.teleportEntity(it, index, location)
        }
    }

    /**
     * 修改实体位置
     */
    fun teleport(entityPosition: EntityPosition) {
        this.teleport(entityPosition.toLocation())
    }

    /**
     * 修改实体位置，并继承实体本身的视角
     */
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

    fun registerController(controller: Controller) {
        unregisterController(controller::class)
        this.controller.add(controller)
    }

    fun <T : Controller> unregisterController(controller: KClass<T>) {
        this.controller.removeIf { it.javaClass == controller.java }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Controller> getController(controller: KClass<T>): T? {
        return (this.controller.firstOrNull { it.javaClass == controller.java } ?: return null) as T
    }

    fun getController(): List<Controller> {
        return this.controller.toList()
    }

    fun resetController() {
        this.controller.clear()
    }

    /**
     * 使实体看向某个坐标
     */
    fun controllerLook(location: Location, smooth: Boolean = true, smoothInternal: Float = 22.5f) {
        position.toLocation().add(0.0, entityType.entitySize.height * 0.9, 0.0).also { entityLocation ->
            entityLocation.direction = location.clone().subtract(entityLocation).toVector()
            controllerLook(entityLocation.yaw, entityLocation.pitch, smooth, smoothInternal)
        }
    }

    /**
     * 使实体看向某个视角
     */
    fun controllerLook(yaw: Float, pitch: Float, smooth: Boolean = true, smoothInternal: Float = 22.5f) {
        if (smooth && controller.any { it is GeneralSmoothLook }) {
            val look = getController(GeneralSmoothLook::class)!!
            look.yaw = yaw
            look.pitch = pitch
            look.isReset = true
            look.isLooking = true
            look.interval = smoothInternal
        } else {
            setHeadRotation(yaw, pitch)
        }
    }

    /**
     * 使实体移动到某个坐标
     */
    fun controllerMove(location: Location, pathType: PathType = entityType.getPathType(), speed: Double = 0.2) {
        if (hasVehicle()) {
            return
        }
        if (pathType.supportVersion > version) {
            throw RuntimeException("PathType \"$pathType\" not supported this minecraft version.")
        }
        if (pathType == PathType.FLY) {
            if (controller.none { it is GeneralMove }) {
                throw RuntimeException("Entity flying movement requires GeneralMove.")
            }
        } else {
            if (controller.none { it is GeneralMove } || controller.none { it is GeneralGravity }) {
                throw RuntimeException("Entity walking movement requires GeneralMove and GeneralGravity.")
            }
        }
        val move = getController(GeneralMove::class)!!
        PathFinderProxy.request(position.toLocation(), location, pathType) {
            move.speed = speed
            move.pathType = pathType
            move.resultNavigation = it as ResultNavigation
        }
    }

    /**
     * 使实体停止移动
     */
    fun controllerStill() {
        if (controller.any { it is GeneralMove }) {
            controller.removeIf { it is GeneralMove }
            controller.add(GeneralMove(this))
            removeTag("isMoving")
            removeTag("isJumping")
        }
    }

    fun isVehicle(): Boolean {
        return getPassengers().isNotEmpty()
    }

    fun hasVehicle(): Boolean {
        return getVehicle() != null
    }

    fun getVehicle(): EntityInstance? {
        return manager?.getEntities()?.firstOrNull { uniqueId in it.passengers }
    }

    fun getPassengers(): List<EntityInstance> {
        if (manager == null) {
            return emptyList()
        }
        return passengers.mapNotNull { manager!!.getEntityByUniqueId(it) }.toList()
    }

    fun addPassenger(vararg entity: EntityInstance) {
        if (manager == null || entity.any { it.manager == null }) {
            throw RuntimeException("Entity Manager not initialized.")
        }
        // 禁止套娃
        entity.forEach { it.removePassenger(this) }
        // 实体变动后发包
        if (passengers.addAll(entity.map { it.uniqueId })) {
            forViewers {
                NMS.INSTANCE.updatePassengers(it, index, *getPassengers().map { e -> e.index }.toIntArray())
            }
        }
    }

    fun removePassenger(vararg entity: EntityInstance) {
        if (manager == null || entity.any { it.manager == null }) {
            throw RuntimeException("Entity Manager not initialized.")
        }
        if (passengers.removeAll(entity.map { it.uniqueId })) {
            forViewers {
                NMS.INSTANCE.updatePassengers(it, index, *getPassengers().map { e -> e.index }.toIntArray())
            }
        }
    }

    fun clearPassengers() {
        if (manager == null && getPassengers().isEmpty()) {
            return
        }
        passengers.clear()
        forViewers {
            NMS.INSTANCE.updatePassengers(it, index)
        }
    }

    fun refreshPassenger() {
        if (manager == null) {
            throw RuntimeException("Entity Manager not initialized.")
        }
        forViewers {
            NMS.INSTANCE.updatePassengers(it, index, *getPassengers().map { e -> e.index }.toIntArray())
        }
    }

    fun displayAnimation(animation: BukkitAnimation) {
        forViewers {
            NMS.INSTANCE.sendAnimation(it, index, animation.id)
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

    fun setPose(pose: BukkitPose) {
        setMetadata("pose", pose)
    }

    fun getPose(): BukkitPose {
        return getMetadata("pose")
    }

    fun isControllerMoving(): Boolean {
        return hasTag("isMoving")
    }

    fun isControllerJumping(): Boolean {
        return hasTag("isJumping")
    }

    /**
     * 实体计算（async）
     */
    fun onTick() {
        // 确保客户端显示实体正常
        if (viewPlayers.visibleLock.next()) {
            // 复活
            viewPlayers.getOutsider().forEach { player ->
                if (player.world.name == position.world.name && player.location.distance(position.toLocation()) < Settings.get().visibleDistance) {
                    viewPlayers.visible.add(player.name)
                    visible(player, true)
                }
            }
            // 销毁
            viewPlayers.getViewers().forEach { player ->
                if (player.world.name != position.world.name || player.location.distance(position.toLocation()) > Settings.get().visibleDistance) {
                    viewPlayers.visible.remove(player.name)
                    visible(player, false)
                }
            }
        }
        // 实体逻辑处理
        controller.filter { it.shouldExecute() }.forEach { it.onTick() }
    }

    /**
     * 克隆实体
     */
    fun clone(newId: String, location: Location): EntityInstance? {
        val json = JsonParser.parseString(toJson()).asJsonObject
        json.addProperty("id", newId)
        json.addProperty("uniqueId", UUID.randomUUID().toString().replace("-", ""))
        val entity = AdyeshachAPI.fromJson(json.toString()) ?: return null
        manager?.addEntity(entity)
        entity.manager = manager
        entity.position = EntityPosition.fromLocation(location)
        entity.controller.addAll(controller)
        forViewers {
            entity.addViewer(it)
        }
        return entity
    }
}