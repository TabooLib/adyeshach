@file:Suppress("DuplicatedCode")

package ink.ptms.adyeshach.common.entity

import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import com.ticxo.modelengine.api.ModelEngineAPI
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.AdyeshachSettings
import ink.ptms.adyeshach.api.event.*
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.entity.editor.EditorHandler
import ink.ptms.adyeshach.common.entity.editor.toDisplay
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.entity.manager.ManagerPrivateTemp
import ink.ptms.adyeshach.common.entity.manager.ManagerPublicTemp
import ink.ptms.adyeshach.common.entity.path.PathFinderHandler
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.path.ResultNavigation
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.common.util.Indexs
import ink.ptms.adyeshach.common.util.safeDistance
import ink.ptms.adyeshach.common.util.serializer.UnknownWorldException
import ink.ptms.adyeshach.common.util.signal
import ink.ptms.adyeshach.internal.compat.CompatServerTours
import ink.ptms.adyeshach.internal.trait.impl.updateViewCondition
import io.netty.util.concurrent.CompleteFuture
import io.netty.util.internal.ConcurrentSet
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import taboolib.common.util.Vector
import taboolib.common.util.unsafeLazy
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.function.Consumer

/**
 * @author sky
 * @since 2020-08-04 12:51
 */
@Suppress("UNCHECKED_CAST", "LeakingThis")
abstract class EntityInstance(entityTypes: EntityTypes) : EntityBase(entityTypes) {

    internal val teleportFutures = CopyOnWriteArrayList<CompletableFuture<*>>()

    /**
     * 控制器（AI）
     */
    @Expose
    protected val controller = ConcurrentSet<Controller>()

    /**
     * 骑乘单位
     */
    @Expose
    protected var passengers = ConcurrentSet<String>()

    /**
     * 可视距离
     * 离开该距离后将回收实体
     */
    @Expose
    var visibleDistance = -1.0
        get() = if (field == -1.0) {
            AdyeshachSettings.visibleDistance
        } else {
            field
        }

    /**
     * 加载完成后自动显示，关闭后只能通过 API 控制玩家是否可见
     * 仅限公共单位
     */
    @Expose
    var visibleAfterLoaded = true
        set(value) {
            if (!isPublic()) {
                error("Entity Manager not public.")
            }
            if (!value) {
                clearViewer()
            }
            field = value
        }

    /**
     * 移动速度，用于巡逻特性
     */
    @Expose
    var moveSpeed = 0.2

    /**
     * 模型名称，基于 ModelEngine 插件
     */
    @Expose
    var modelEngineName = ""
        set(value) {
            field = value
            refreshModelEngine()
        }

    /**
     * 模型序号，基于 ModelEngine 插件
     */
    var modelEngineUniqueId: UUID? = null

    /**
     * 实体序号，用于发包
     */
    val index by unsafeLazy { Indexs.nextIndex() }

    /**
     * 单位管理器
     */
    var manager: Manager? = null
        set(value) {
            if (value != null && field != null) {
                error("Entity Manager has been initialized.")
            }
            field = value
        }

    /**
     * 观察者容器
     */
    val viewPlayers = ViewPlayers(this)

    /**
     * 是否冻结实体，启用后无法移动
     */
    var isFreeze: Boolean
        set(value) {
            if (value) {
                setTag("isFreeze", "true")
                setTag("isFreeze_Time", System.currentTimeMillis())
            } else {
                removeTag("isFreeze")
                removeTag("isFreeze_Time")
            }
        }
        get() = getTag("isFreeze") == "true"

    /**
     * 是否禁用控制器
     */
    var isNoAi: Boolean
        set(value) {
            if (value) {
                setTag("isNoAi", "true")
            } else {
                removeTag("isNoAi")
            }
        }
        get() = getTag("isNoAi") == "true"

    /**
     * 是否被删除，执行 delete() 方法后该属性为 true
     * 该属性仅做标记无实际意义
     */
    var isDeleted = false

    /**
     * 获取单位展示名称（没有 customName 则获取 EntityType 名称）
     */
    fun getDisplayName(): String {
        return when {
            this is AdyHuman -> {
                getName()
            }

            getCustomName().isEmpty() -> {
                entityType.name.lowercase().toDisplay()
            }

            else -> {
                getCustomName()
            }
        }
    }

    /**
     * 切换可视状态
     */
    abstract fun visible(viewer: Player, visible: Boolean): Boolean

    /**
     * 内部调用方法，用于第三方事件处理
     */
    protected fun spawn(viewer: Player, spawn: Runnable): Boolean {
        if (AdyeshachEntityVisibleEvent(this, viewer, true).call()) {
            // 若未生成 ModelEngine 模型则发送原版数据包
            // 这可能会导致 getEntityFromClientUniqueId 方法无法获取
            if (!showModelEngine(viewer)) {
                spawn.run()
            }
            // 更新单位属性
            updateEntityMetadata(viewer)
            // 更新单位视角
            setHeadRotation(position.yaw, position.pitch, true)
            // 关联实体初始化
            submit(delay = 5) {
                refreshPassenger(viewer, error = false)
            }
            return true
        }
        return false
    }

    /**
     * 内部调用方法
     */
    protected fun destroy(viewer: Player, destroy: Runnable): Boolean {
        if (AdyeshachEntityVisibleEvent(this, viewer, false).call()) {
            // 销毁模型
            if (!hideModelEngine(viewer)) {
                destroy.run()
            }
            return true
        }
        return false
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
     * 遍历所有有效观察者
     */
    fun forViewers(viewer: Consumer<Player>) {
        viewPlayers.getViewPlayers().forEach { viewer.accept(it) }
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
     * 是否为观察者
     */
    fun isViewer(viewer: Player): Boolean {
        return viewer.name in viewPlayers.viewers
    }

    /**
     * 是否拥有有效观察者
     */
    fun hasViewer(): Boolean {
        return viewPlayers.getViewPlayers().isNotEmpty()
    }

    /**
     * 是否为真实观察者（在观察范围内）
     */
    fun isVisibleViewer(viewer: Player): Boolean {
        return viewer.name in viewPlayers.viewers && viewer.name in viewPlayers.visible
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
     * 被移除管理器后该单位将失去所有主动行为
     * 包括玩家类型的皮肤刷新
     *
     * 同时该单位将无法被指令控制
     */
    fun remove() {
        if (manager != null) {
            manager!!.remove(this)
            manager = null
            AdyeshachEntityRemoveEvent(this).call()
        }
    }

    /**
     * 删除实体，并销毁
     */
    fun delete() {
        isDeleted = true
        destroy()
        remove()
    }

    /**
     * 修改实体位置
     */
    @Deprecated("Use teleportFuture instead", ReplaceWith("teleportFuture(location)"))
    fun teleport(location: Location) {
        teleportFuture(location)
    }

    /**
     * 修改实体位置
     */
    fun teleportFuture(location: Location): CompletableFuture<*> {
        val event = AdyeshachEntityTeleportEvent(this, location)
        if (event.call()) {
            position = EntityPosition.fromLocation(event.location)
            // 修改世界
            if (position.world.name != location.world?.name) {
                destroy()
                respawn()
            } else {
                val futures = arrayListOf<CompletableFuture<*>>()
                forViewers { futures += NMS.INSTANCE.teleportEntity(it, index, location) }
                return CompletableFuture.allOf(*futures.toTypedArray()).signal(teleportFutures)
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    /**
     * 修改实体位置
     */
    @Deprecated("Use teleportFuture instead", ReplaceWith("teleportFuture(entityPosition)"))
    fun teleport(entityPosition: EntityPosition) {
        teleport(entityPosition.toLocation())
    }

    /**
     * 修改实体位置
     */
    fun teleportFuture(entityPosition: EntityPosition): CompletableFuture<*> {
        return teleportFuture(entityPosition.toLocation())
    }

    /**
     * 修改实体位置，并继承实体本身的视角
     */
    @Deprecated("Use teleportFuture instead", ReplaceWith("teleportFuture(x, y, z)"))
    fun teleport(x: Double, y: Double, z: Double) {
        teleportFuture(x, y, z)
    }

    /**
     * 修改实体位置，并继承实体本身的视角
     */
    fun teleportFuture(x: Double, y: Double, z: Double): CompletableFuture<*> {
        if (x.isFinite() && y.isFinite() && z.isFinite()) {
            return teleportFuture(position.toLocation().run {
                this.x = x
                this.y = y
                this.z = z
                this
            })
        }
        return CompletableFuture.completedFuture(null)
    }

    /**
     * 修改实体视角
     */
    fun setHeadRotation(yaw: Float, pitch: Float) {
        setHeadRotation(yaw, pitch, false)
    }

    /**
     * 修改实体视角
     */
    fun setHeadRotation(yaw: Float, pitch: Float, force: Boolean = false) {
        val event = AdyeshachHeadRotationEvent(this, yaw, pitch)
        if (event.call()) {
            // 如果数字变更，则更新视角
            val hasUpdate = position.yaw != yaw || position.pitch != pitch
            if (hasUpdate || force) {
                position = position.run {
                    this.yaw = event.yaw
                    this.pitch = event.pitch
                    this
                }
                forViewers { NMS.INSTANCE.setHeadRotation(it, index, event.yaw, event.pitch) }
            }
        }
    }

    /**
     * 获取所有控制器
     */
    fun getController(): List<Controller> {
        return this.controller.toList()
    }

    /**
     * 获取控制器
     */
    fun <T : Controller> getController(controller: Class<T>): T? {
        return this.controller.firstOrNull { it.javaClass == controller } as? T
    }

    /**
     * 注册控制器
     */
    fun registerController(controller: Controller) {
        unregisterController(controller::class.java)
        this.controller.add(controller)
    }

    /**
     * 注销控制器
     */
    fun <T : Controller> unregisterController(controller: Class<T>) {
        if (controller == GeneralMove::class.java) {
            removeTag("isMoving")
        }
        this.controller.removeIf { it.javaClass == controller }
    }

    /**
     * 重置控制器
     */
    fun resetController() {
        if (getController(GeneralMove::class.java) != null) {
            removeTag("isMoving")
        }
        this.controller.clear()
    }

    /**
     * 单位是否在尝试移动（等待寻路的过程）
     */
    fun isTryMoving(): Boolean {
        return hasTag("tryMoving")
    }

    /**
     * 单位是否在移动状态
     */
    fun isControllerMoving(): Boolean {
        return hasTag("isMoving")
    }

    /**
     * 单位是否在跳跃状态
     */
    fun isControllerJumping(): Boolean {
        return hasTag("isJumping")
    }

    /**
     * 单位是否在地表（依赖 Gravity 控制器）
     */
    fun isControllerOnGround(): Boolean {
        return getController(GeneralGravity::class.java)?.isOnGround ?: error("no gravity")
    }

    /**
     * 使实体看向某个坐标
     */
    fun controllerLook(location: Location, smooth: Boolean = false, smoothInternal: Float = 22.5f) {
        position.toLocation().add(0.0, entityType.entitySize.height * 0.9, 0.0).also { entityLocation ->
            entityLocation.direction = location.clone().subtract(entityLocation).toVector()
            controllerLook(entityLocation.yaw, entityLocation.pitch, smooth, smoothInternal)
        }
    }

    /**
     * 使实体看向某个视角
     */
    fun controllerLook(yaw: Float, pitch: Float, smooth: Boolean = false, smoothInternal: Float = 22.5f) {
        if (smooth && controller.any { it is GeneralSmoothLook }) {
            val look = getController(GeneralSmoothLook::class.java)!!
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
    fun controllerMove(location: Location, pathType: PathType = entityType.getPathType(), speed: Double = moveSpeed) {
        if (hasVehicle()) {
            return
        }
        if (pathType == PathType.FLY) {
            if (controller.none { it is GeneralMove }) {
                error("Entity flying movement requires GeneralMove.")
            }
        } else {
            if (controller.none { it is GeneralMove } || controller.none { it is GeneralGravity }) {
                error("Entity walking movement requires GeneralMove and GeneralGravity.")
            }
        }
        val move = getController(GeneralMove::class.java)!!
        setTag("tryMoving", "true")
        PathFinderHandler.request(position.toLocation(), location, pathType) {
            if ((it as ResultNavigation).pointList.isEmpty()) {
                return@request
            }
            move.speed = speed
            move.target = location
            move.pathType = pathType
            move.resultNavigation = it
            removeTag("tryMoving")
        }
    }

    /**
     * 使实体根据特定路径移动到某个坐标
     */
    fun controllerMoveWithPathList(
        location: Location,
        pathList: List<org.bukkit.util.Vector>,
        pathType: PathType = entityType.getPathType(),
        speed: Double = moveSpeed,
    ) {
        if (hasVehicle()) {
            return
        }
        if (pathType == PathType.FLY) {
            if (controller.none { it is GeneralMove }) {
                error("Entity flying movement requires GeneralMove.")
            }
        } else {
            if (controller.none { it is GeneralMove } || controller.none { it is GeneralGravity }) {
                error("Entity walking movement requires GeneralMove and GeneralGravity.")
            }
        }
        val move = getController(GeneralMove::class.java)!!
        move.speed = speed
        move.target = location
        move.pathType = pathType
        move.resultNavigation = ResultNavigation(pathList, 0L, 0L)
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

    /**
     * 播放动画
     */
    fun displayAnimation(animation: BukkitAnimation) {
        forViewers {
            NMS.INSTANCE.sendAnimation(it, index, animation.id)
            // 模型效果
            if (AdyeshachAPI.modelEngineHooked && modelEngineUniqueId != null) {
                ModelEngineAPI.api.modelManager.getModeledEntity(modelEngineUniqueId)?.hurt()
            }
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
            error("Entity Manager not initialized.")
        }
        if (entity.any { it.manager != manager }) {
            error("Entity Manager not identical.")
        }
        entity.forEach {
            it.removePassenger(this)
            if (AdyeshachEntityVehicleEnterEvent(it, this).call()) {
                passengers.add(it.uniqueId)
            }
        }
        refreshPassenger()
    }

    fun removePassenger(vararg entity: EntityInstance) {
        if (manager == null || entity.any { it.manager == null }) {
            error("Entity Manager not initialized.")
        }
        if (entity.any { it.manager != manager }) {
            error("Entity Manager not identical.")
        }
        entity.forEach {
            if (AdyeshachEntityVehicleEnterEvent(it, this).call()) {
                passengers.remove(it.uniqueId)
                val en = manager?.getEntityByUniqueId(it.uniqueId)
                en?.teleport(en.getLocation())
            }
        }
        refreshPassenger()
    }

    fun removePassenger(vararg id: String) {
        if (manager == null) {
            error("Entity Manager not initialized.")
        }
        removePassenger(*getPassengers().filter { it.id in id }.toTypedArray())
    }

    fun clearPassengers() {
        if (manager == null && getPassengers().isEmpty()) {
            return
        }
        removePassenger(*getPassengers().toTypedArray())
    }

    fun refreshPassenger(viewer: Player, error: Boolean = true) {
        if (manager == null) {
            if (error) {
                error("Entity Manager not initialized.")
            }
        }
        NMS.INSTANCE.updatePassengers(viewer, index, *getPassengers().map { e -> e.index }.toIntArray())
        getVehicle()?.refreshPassenger(viewer)
    }

    fun refreshPassenger() {
        if (manager == null) {
            error("Entity Manager not initialized.")
        }
        forViewers { NMS.INSTANCE.updatePassengers(it, index, *getPassengers().map { e -> e.index }.toIntArray()) }
        val vehicle = getVehicle() ?: return
        forViewers { vehicle.refreshPassenger(it) }
    }

    fun isInVisibleDistance(player: Player): Boolean {
        return player.location.safeDistance(getLocation()) < visibleDistance
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

    var ticksFrozenInPowderedSnow: Int
        get() = getMetadata("ticksFrozenInPowderedSnow")
        set(value) {
            setMetadata("ticksFrozenInPowderedSnow", value)
        }

    /**
     * 设置实体元数据并更新数据包
     */
    override fun setMetadata(key: String, value: Any): Boolean {
        val result = super.setMetadata(key, value)
        if (result) {
            getAvailableEntityMeta().first { meta -> meta.key.equals(key, true) }.updateEntityMetadata(this)
        }
        return result
    }

    /**
     * 更新实体元数据（所有有效的观察者）
     */
    open fun updateEntityMetadata() {
        forViewers {
            updateEntityMetadata(it)
        }
    }

    /**
     * 更新实体元数据
     */
    open fun updateEntityMetadata(viewer: Player) {
        val metadata = generateEntityMetadata(viewer)
        if (metadata.isNotEmpty()) {
            NMS.INSTANCE.updateEntityMetadata(viewer, this.index, *metadata)
        }
    }

    /**
     * 生成元数据
     */
    open fun generateEntityMetadata(player: Player): Array<Any> {
        return getAvailableEntityMeta().mapNotNull { it.generateMetadata(player, this) }.toTypedArray()
    }

    /**
     * 发送向量数据包
     */
    open fun sendVelocity(vector: Vector) {
        forViewers { NMS.INSTANCE.updateEntityVelocity(it, index, org.bukkit.util.Vector(vector.x, vector.y, vector.z)) }
    }

    /**
     * 打开编辑器
     */
    open fun openEditor(player: Player) {
        EditorHandler.open(player, this)
    }

    /**
     * 实体计算（async）
     */
    open fun onTick() {
//        if (!AdyeshachEntityTickEvent(this).call()) {
//            return
//        }
        // 临时修复
        updateViewCondition()
        // 确保客户端显示实体正常
        if (viewPlayers.visibleRefreshLocker.hasNext()) {
            // 复活
            viewPlayers.getOutsidePlayers().forEach { player ->
                if (isInVisibleDistance(player) && visible(player, true)) {
                    viewPlayers.visible.add(player.name)
                }
            }
            // 销毁
            viewPlayers.getViewPlayers().forEach { player ->
                if (!isInVisibleDistance(player) && visible(player, false) && !CompatServerTours.isRoutePlaying(player)) {
                    viewPlayers.visible.remove(player.name)
                }
            }
        }
        // 只有存在可见玩家时才会处理实体控制器
        if (viewPlayers.hasVisiblePlayer() && !isNoAi) {
            val loc = getLocation()
            // 实体逻辑处理
            controller.filter { it.shouldExecute() && getWorld().isChunkLoaded(loc.blockX shr 4, loc.blockZ shr 4) }.forEach {
                when {
                    it is Controller.Pre -> {
                        controller.add(it.controller.generator.apply(this))
                        controller.remove(it)
                    }

                    it.isAsync() -> {
                        pool.submit { it.onTick() }
                    }

                    else -> {
                        it.onTick()
                    }
                }
            }
        }
    }

    /**
     * 克隆实体
     */
    @Throws(UnknownWorldException::class)
    open fun clone(newId: String, location: Location, manager: Manager? = null): EntityInstance? {
        val json = JsonParser().parse(toJson()).asJsonObject
        json.addProperty("id", newId)
        json.addProperty("uniqueId", UUID.randomUUID().toString().replace("-", ""))
        val entity = AdyeshachAPI.fromJson(json.toString()) ?: return null
        (manager ?: this.manager)?.addEntity(entity)
        entity.tag.putAll(tag)
        entity.persistentTag.putAll(persistentTag)
        entity.manager = (manager ?: this.manager)
        entity.position = EntityPosition.fromLocation(location)
        entity.passengers.clear()
        // 复制关联单位
        getPassengers().forEachIndexed { index, passenger ->
            passenger.clone("${newId}_passenger_$index", location)?.let { entity.addPassenger(it) }
        }
        // 复制观察者
        forViewers { entity.addViewer(it) }
        return entity
    }

    open fun showModelEngine(viewer: Player): Boolean {
        if (AdyeshachAPI.modelEngineHooked) {
            val modelManager = ModelEngineAPI.api.modelManager
            if (modelEngineUniqueId != null) {
                modelManager.getModeledEntity(modelEngineUniqueId)?.addPlayer(viewer) ?: return false
                return true
            } else if (modelEngineName.isNotBlank()) {
                return refreshModelEngine()
            }
        }
        return false
    }

    open fun hideModelEngine(viewer: Player): Boolean {
        if (AdyeshachAPI.modelEngineHooked) {
            val modelManager = ModelEngineAPI.api.modelManager
            if (modelEngineUniqueId != null) {
                modelManager.getModeledEntity(modelEngineUniqueId)?.removePlayer(viewer) ?: return false
                return true
            }
        }
        return false
    }

    open fun refreshModelEngine(): Boolean {
        if (AdyeshachAPI.modelEngineHooked) {
            val modelManager = ModelEngineAPI.api.modelManager
            // 删除模型
            if (modelEngineUniqueId != null) {
                val modeledEntity = modelManager.getModeledEntity(modelEngineUniqueId)
                if (modeledEntity != null) {
                    modeledEntity.clearModels()
                    forViewers {
                        modeledEntity.removePlayer(it)
                    }
                    modelManager.removeModeledEntity(modelEngineUniqueId)
                    modelEngineUniqueId = null
                    // 是否恢复单位
                    if (modelEngineName.isBlank()) {
                        respawn()
                    }
                }
            }
            // 创建模型
            if (modelEngineName.isNotBlank()) {
                val entityModeled = EntityModeled(this)
                val model = modelManager.createActiveModel(modelEngineName)
                if (model == null) {
                    warning("Failed to load model: $modelEngineName")
                    return false
                }
                val modeledEntity = modelManager.createModeledEntity(entityModeled)
                if (modeledEntity == null) {
                    warning("Failed to create modeled entity")
                    return false
                }
                try {
                    modeledEntity.addActiveModel(model)
                } catch (ex: NullPointerException) {
                    warning("Failed to load model: $modelEngineName (${ex.message})")
                    return false
                }
                destroy()
                modeledEntity.isInvisible = true
                val nameTag = modeledEntity.nametagHandler.allTags.firstOrNull()
                if (nameTag != null) {
                    modeledEntity.nametagHandler.setCustomName(nameTag, getCustomName())
                    modeledEntity.nametagHandler.setCustomNameVisibility(nameTag, isCustomNameVisible())
                }
                modelEngineUniqueId = entityModeled.modelUniqueId
                // 首次加载模型的时候还不存在任何观察者，因此需要延迟添加
                // 否则会出现模型消失的问题
                // 于 2022-05-16 日由阿瑞（1484813603）反馈
                submit { forViewers { modeledEntity.addPlayer(it) } }
                return true
            }
        }
        return false
    }

    open fun updateModelEngineNameTag() {
        if (AdyeshachAPI.modelEngineHooked) {
            val modelManager = ModelEngineAPI.api.modelManager
            if (modelEngineUniqueId != null) {
                val modeledEntity = modelManager.getModeledEntity(modelEngineUniqueId) ?: return
                val nameTag = modeledEntity.nametagHandler.allTags.firstOrNull()
                if (nameTag != null) {
                    modeledEntity.nametagHandler.setCustomName(nameTag, getCustomName())
                    modeledEntity.nametagHandler.setCustomNameVisibility(nameTag, isCustomNameVisible())
                }
            }
        }
    }

    companion object {

        val pool = Executors.newFixedThreadPool(16)!!
    }
}