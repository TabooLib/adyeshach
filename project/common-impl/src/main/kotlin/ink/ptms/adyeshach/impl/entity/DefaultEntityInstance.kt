package ink.ptms.adyeshach.impl.entity

import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.bukkit.BukkitAnimation
import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.bukkit.data.EntityPosition
import ink.ptms.adyeshach.core.entity.*
import ink.ptms.adyeshach.core.entity.controller.Controller
import ink.ptms.adyeshach.core.entity.manager.Manager
import ink.ptms.adyeshach.core.entity.path.InterpolatedLocation
import ink.ptms.adyeshach.core.entity.path.PathFinderHandler
import ink.ptms.adyeshach.core.entity.path.ResultNavigation
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.core.util.plus
import ink.ptms.adyeshach.core.util.safeDistanceIgnoreY
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.DefaultAdyeshachEntityFinder.Companion.clientEntityMap
import ink.ptms.adyeshach.impl.VisualTeam
import ink.ptms.adyeshach.impl.entity.controller.BionicSight
import ink.ptms.adyeshach.impl.entity.handler.EntityHandlerFactory
import ink.ptms.adyeshach.impl.entity.handler.PositionHandler
import ink.ptms.adyeshach.impl.util.ChunkAccess
import ink.ptms.adyeshach.impl.util.Indexs
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import taboolib.common5.Baffle
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.nms.MinecraftVersion
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
 *
 * @author 坏黑
 * @since 2022/6/19 21:26
 */
@Suppress("LeakingThis")
abstract class DefaultEntityInstance(entityType: EntityTypes = EntityTypes.ZOMBIE) : DefaultEntityBase(entityType), EntityInstance, InternalEntity {

    // ═══════════════════════════════════════════════════════════════════════════════
    // 位置属性（委托到 clientPosition）
    // ═══════════════════════════════════════════════════════════════════════════════

    override val x get() = clientPosition.x
    override val y get() = clientPosition.y
    override val z get() = clientPosition.z
    override val yaw get() = clientPosition.yaw
    override val pitch get() = clientPosition.pitch
    override val world: World get() = clientPosition.world
    override val chunkX get() = (x / 16).toInt()
    override val chunkZ get() = (z / 16).toInt()

    // ═══════════════════════════════════════════════════════════════════════════════
    // 核心属性
    // ═══════════════════════════════════════════════════════════════════════════════

    override val index: Int = Indexs.nextIndex()
    override val viewPlayers = EntityHandlerFactory.createViewPlayers(this)
    override val entitySize = Adyeshach.api().getEntityTypeRegistry().getEntitySize(entityType)
    override val entityPathType = Adyeshach.api().getEntityTypeRegistry().getEntityPathType(entityType)

    override var isRemoved = false
    override var isCreated = false
    @Expose override var isNitwit = false
    @Expose override var moveSpeed = 0.2
    override var brain: Brain? = EntityHandlerFactory.createBrain(this)

    override var useClientEntityMap = true
    override var isRotationFixOnSpawn = true
    override var isPassengerRefreshOnSpawn = true
    override var isDisableVisibleEvent = false
    override var isDisableVehicleCheckOnTick = false
    override var isDisableVehicleRotationSync = false
    override var clientPositionFixed = System.currentTimeMillis()
    override var clientPositionUpdateInterval: Baffle = Baffle.of(Adyeshach.api().getEntityTypeRegistry().getEntityClientUpdateInterval(entityType))
    override var isIgnoredClientPositionUpdateInterval = true

    // ═══════════════════════════════════════════════════════════════════════════════
    // 记分板相关属性（需要同步 VisualTeam）
    // ═══════════════════════════════════════════════════════════════════════════════

    @Expose
    override var isNameTagVisible = true
        set(value) {
            if (!value) canSeeInvisible = false
            field = value
            VisualTeam.updateTeam(this)
        }

    @Expose
    override var isCollision = true
        set(value) {
            if (!value) canSeeInvisible = false
            field = value
            VisualTeam.updateTeam(this)
        }

    @Expose
    override var glowingColor: ChatColor = ChatColor.WHITE
        set(value) {
            if (value != ChatColor.WHITE) canSeeInvisible = true
            field = value
            VisualTeam.updateTeam(this)
        }

    @Expose
    override var canSeeInvisible = false
        set(value) {
            if (!isNameTagVisible || !isCollision || glowingColor != ChatColor.WHITE) return
            field = value
            VisualTeam.updateTeam(this)
        }

    // ═══════════════════════════════════════════════════════════════════════════════
    // 可见性与显示属性
    // ═══════════════════════════════════════════════════════════════════════════════

    @Expose
    override var visibleDistance = -1.0
        get() = if (field == -1.0) AdyeshachSettings.visibleDistance else field

    @Expose
    override var visibleAfterLoaded = true
        set(value) {
            if (!isPublic()) errorBy("error-cannot-set-visible-after-loaded-for-private-entity")
            if (!value) clearViewer()
            field = value
        }

    // ═══════════════════════════════════════════════════════════════════════════════
    // ModelEngine 支持
    // ═══════════════════════════════════════════════════════════════════════════════

    var modelEngineUniqueId: UUID? = null
    var modelEngineOptions: ModelEngineOptions? = null

    @Expose
    var modelEngineName = ""
        set(value) {
            field = value
            if (this is ModelEngine) refreshModelEngine()
        }

    // ═══════════════════════════════════════════════════════════════════════════════
    // 关联实体数据
    // ═══════════════════════════════════════════════════════════════════════════════

    @Expose var passengers = EntityRefSet(this)
    @Expose var companions = EntityRefSet(this)
    @Expose var controller = ConcurrentSkipListSet(Comparator.comparing(Controller::id))
    var cacheHostEntity: EntityInstance? = null
    var cacheVehicleEntity: EntityInstance? = null
    val attachedEntity = ConcurrentHashMap<Int, Vector>()

    // ═══════════════════════════════════════════════════════════════════════════════
    // 处理器
    // ═══════════════════════════════════════════════════════════════════════════════

    val movementHandler = EntityHandlerFactory.createMovementHandler(this)
    val visibilityHandler = EntityHandlerFactory.createVisibilityHandler(this)
    val passengerHandler = EntityHandlerFactory.createPassengerHandler(this)
    val controllerHandler = EntityHandlerFactory.createControllerHandler(this)
    val positionHandler = EntityHandlerFactory.createPositionHandler(this)
    val lifecycleHandler = EntityHandlerFactory.createLifecycleHandler(this)
    val customMetaHandler = EntityHandlerFactory.createCustomMetaHandler(this)
    val genericEntityHandler = EntityHandlerFactory.createGenericEntityHandler(this)
    val companionHandler = EntityHandlerFactory.createCompanionHandler(this)
    val metaHandler = EntityHandlerFactory.createMetaHandler(this)
    val tagHandler = EntityHandlerFactory.createTagHandler(this)
    val serializationHandler = EntityHandlerFactory.createSerializationHandler(this)
    var bionicSight: BionicSight? = EntityHandlerFactory.createBionicSight(this)

    // ═══════════════════════════════════════════════════════════════════════════════
    // 位置状态
    // ═══════════════════════════════════════════════════════════════════════════════

    @Expose
    var clientPosition: EntityPosition = position
        set(value) {
            // 平移时同步 xyz，clientBodyPosition.yaw 保持身体朝向（headYaw 在 clientPosition）
            // 跨世界时同时替换 world，避免身体位置 API 残留旧世界
            if (field.world != value.world || field.x != value.x || field.y != value.y || field.z != value.z) {
                clientBodyPosition = clientBodyPosition.copy(
                    world = value.world,
                    x = value.x,
                    y = value.y,
                    z = value.z,
                )
            }
            field = value.clone()
        }
        get() {
            if (field.isZero() && field != position) field = position
            return field
        }

    var clientBodyPosition: EntityPosition = position
        set(value) { field = value.clone() }
        get() {
            if (field.isZero() && field != position) field = position
            return field
        }

    var deltaMovement: Vector = Vector(0.0, 0.0, 0.0)
        set(value) { field = value.clone() }

    override var moveFrames: InterpolatedLocation? = null
        set(value) {
            field = value
            DefaultAdyeshachAPI.localEventBus.callMove(this, value != null)
        }

    override var moveTarget: Location? = null
        set(value) {
            field = value
            handleMoveTargetChange()
        }

    private fun handleMoveTargetChange() {
        if (isNitwit || moveTarget == null) {
            if (moveFrames != null) {
                moveFrames = null
                tag.remove(StandardTags.IS_MOVING)
            }
            return
        }
        setTag(StandardTags.IS_PATHFINDING, true)
        PathFinderHandler.request(position.toLocation(), moveTarget!!, entityPathType) {
            it as ResultNavigation
            removeTag(StandardTags.IS_PATHFINDING)
            controllerMoveBy(it.pointList.map { v -> v.toLocation(world) })
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // 管理器
    // ═══════════════════════════════════════════════════════════════════════════════

    override var manager: Manager? = null
        set(value) {
            if (field != null && value != null) {
                errorBy("error-manager-has-been-initialized")
            }
            field = value
            updateManagerTags(value)
        }

    private fun updateManagerTags(manager: Manager?) {
        if (manager == null || manager !is TickService) {
            tag[StandardTags.ISOLATED] = true
            return
        }
        tag.remove(StandardTags.ISOLATED)
        if (manager.isPublic()) {
            tag[StandardTags.IS_PUBLIC] = true
            tag.remove(StandardTags.IS_PRIVATE)
        } else {
            tag[StandardTags.IS_PRIVATE] = true
            tag.remove(StandardTags.IS_PUBLIC)
        }
        if (manager.isTemporary()) {
            tag[StandardTags.IS_TEMPORARY] = true
        } else {
            tag.remove(StandardTags.IS_TEMPORARY)
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // 核心方法
    // ═══════════════════════════════════════════════════════════════════════════════

    override fun isPublic() = manager?.isPublic() == true
    override fun isTemporary() = manager?.isTemporary() == true

    override fun onTick() {
        // 1.21.8 客户端对传送/相对移动判定更严：nitwit 若跳过 syncPosition，外部 teleport 会每帧硬传、表现为平移
        if (!allowSyncPosition()) {
            return
        }
        if (!isNitwit) {
            movementHandler.handleMove()
            brain?.tick()
            bionicSight?.tick()
        }
        movementHandler.syncPosition()
    }

    override fun isHide() = getPersistentTag("hide").toBoolean()
    override fun hide(value: Boolean) = setPersistentTag("hide", value.toString())

    /**
     * 是否允许本 tick 做位置同步（可见玩家 + 区块已加载）
     *
     * 与 [isNitwit] 解耦。1.21.8 起客户端逻辑变化后，nitwit 实体也必须走相对移动同步，
     * 否则每帧 Entity Teleport 无法触发正确的移动插值/行走表现。
     */
    fun allowSyncPosition(): Boolean {
        return viewPlayers.hasVisiblePlayer() && ChunkAccess.getChunkAccess(world).isChunkLoaded(chunkX, chunkZ)
    }

    override fun clone(newId: String, location: Location, manager: Manager?): EntityInstance? {
        val json = JsonParser().parse(toJson()).asJsonObject
        json.addProperty("id", newId)
        json.addProperty("uniqueId", UUID.randomUUID().toString().replace("-", ""))
        val entity = Adyeshach.api().getEntitySerializer().fromJson(json.toString()) as DefaultEntityInstance
        entity.tag.putAll(tag)
        entity.persistentTag.putAll(persistentTag)
        entity.manager = manager ?: this.manager
        entity.position = EntityPosition.fromLocation(location)
        entity.clientPosition = entity.position
        entity.passengers.clear()
        forViewers { entity.addViewer(it) }
        getPassengers().forEachIndexed { i, p ->
            p.clone("${newId}_passenger_$i", location)?.let { entity.addPassenger(it) }
        }
        entity.manager?.add(entity)
        return entity
    }

    override fun setDerived(id: String) {
        isNitwit = true
        setPersistentTag(StandardTags.DERIVED, id)
    }

    override fun isInVisibleDistance(player: Player): Boolean {
        return player.location.safeDistanceIgnoreY(getLocation()) < visibleDistance
    }

    override fun getLocation(): Location = clientPosition.toLocation()

    /**
     * 生成包与外部 API 用的身体位置，yaw 由 [PositionHandler.spawnBodyLocation] 统一计算
     */
    override fun getBodyLocation(): Location {
        return positionHandler.spawnBodyLocation()
    }

    override fun getEyeLocation(): Location = clientPosition.toLocation().plus(y = entitySize.height)

    // ═══════════════════════════════════════════════════════════════════════════════
    // 伴生实体可见性（内部方法）
    // ═══════════════════════════════════════════════════════════════════════════════

    open fun syncCompanionVisible(viewer: Player, visible: Boolean) {
        companions.instances.forEach {
            it as DefaultEntityInstance
            // 同步到伴生实体
            // 伴生可见性经 CompanionHandler 做 ACL 与 viewers 继承，禁止直接改写 visible
            it.companionHandler.syncVisibleFromHost(viewer, visible)
        }
    }

    open fun handleCompanionVisible(viewer: Player, visible: Boolean) {}

    // ═══════════════════════════════════════════════════════════════════════════════
    // 委托方法 - Lifecycle
    // ═══════════════════════════════════════════════════════════════════════════════

    override fun spawn(location: Location) = lifecycleHandler.spawn(location)
    override fun respawn() = lifecycleHandler.respawn()
    override fun despawn(destroyPacket: Boolean, removeFromManager: Boolean) = lifecycleHandler.despawn(destroyPacket, removeFromManager)
    override fun prepareSpawn(viewer: Player, spawn: Runnable) = lifecycleHandler.prepareSpawn(viewer, spawn)
    override fun prepareDestroy(viewer: Player, destroy: Runnable) = lifecycleHandler.prepareDestroy(viewer, destroy)
    override fun checkVisible() = visibilityHandler.checkVisible()

    // ═══════════════════════════════════════════════════════════════════════════════
    // 委托方法 - Position
    // ═══════════════════════════════════════════════════════════════════════════════

    override fun teleport(entityPosition: EntityPosition) = positionHandler.teleport(entityPosition)
    override fun teleport(x: Double, y: Double, z: Double) = positionHandler.teleport(x, y, z)
    override fun teleport(location: Location) = positionHandler.teleport(location)
    override fun setVelocity(vector: Vector) = positionHandler.setVelocity(vector)
    override fun setVelocity(x: Double, y: Double, z: Double) = positionHandler.setVelocity(x, y, z)
    override fun getVelocity() = positionHandler.getVelocity()
    override fun setHeadRotation(location: Location, forceUpdate: Boolean) = positionHandler.setHeadRotation(location, forceUpdate)
    override fun setHeadAndBodyRotation(location: Location) = positionHandler.setHeadAndBodyRotation(location)
    override fun setHeadAndBodyRotation(yaw: Float, pitch: Float) = positionHandler.setHeadAndBodyRotation(yaw, pitch)
    override fun setHeadRotation(yaw: Float, pitch: Float, forceUpdate: Boolean) = positionHandler.setHeadRotation(yaw, pitch, forceUpdate)
    override fun setBodyRotation(yaw: Float) = positionHandler.setBodyRotation(yaw)
    override fun displayBodyYaw(): Float {
        return EntityPosition.normalizeYaw(positionHandler.runtimeBodyYaw())
    }
    override fun refreshPosition() = positionHandler.refreshPosition()

    // ═══════════════════════════════════════════════════════════════════════════════
    // 委托方法 - Rideable
    // ═══════════════════════════════════════════════════════════════════════════════

    override fun isVehicle() = passengerHandler.isVehicle()
    override fun hasVehicle() = passengerHandler.hasVehicle()
    override fun getVehicle() = cacheVehicleEntity
    override fun hasPassengers() = passengerHandler.hasPassengers()
    override fun getPassengers() = passengerHandler.getPassengers()
    override fun addPassenger(vararg entity: EntityInstance) = passengerHandler.addPassenger(*entity)
    override fun removePassenger(vararg entity: EntityInstance) = passengerHandler.removePassenger(*entity)
    override fun removePassenger(vararg id: String) = passengerHandler.removePassenger(*id)
    override fun clearPassengers() = passengerHandler.clearPassengers()
    override fun refreshPassenger(viewer: Player) = passengerHandler.refreshPassenger(viewer)
    override fun refreshPassenger() = passengerHandler.refreshPassenger()
    override fun verifyPassenger() = passengerHandler.verifyPassenger()

    // ═══════════════════════════════════════════════════════════════════════════════
    // 委托方法 - Controllable
    // ═══════════════════════════════════════════════════════════════════════════════

    override var isFreeze by controllerHandler::isFreeze
    override fun getController() = controllerHandler.getController()
    override fun <T : Controller> getController(controller: String): T? = controllerHandler.getController(controller)
    override fun <T : Controller> getController(controller: Class<T>): T? = controllerHandler.getController(controller)
    override fun registerController(controller: Controller) = controllerHandler.registerController(controller)
    override fun unregisterController(controller: String) = controllerHandler.unregisterController(controller)
    override fun unregisterController(controller: Controller) = controllerHandler.unregisterController(controller)
    override fun <T : Controller> unregisterController(controller: Class<T>) = controllerHandler.unregisterController(controller)
    override fun resetController() = controllerHandler.resetController()
    override fun controllerLookAt(entity: Entity) = controllerHandler.controllerLookAt(entity)
    override fun controllerLookAt(entity: Entity, yMaxRotSpeed: Float, xMaxRotAngle: Float) = controllerHandler.controllerLookAt(entity, yMaxRotSpeed, xMaxRotAngle)
    override fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double) = controllerHandler.controllerLookAt(wantedX, wantedY, wantedZ)
    override fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double, yMaxRotSpeed: Float) = controllerHandler.controllerLookAt(wantedX, wantedY, wantedZ, yMaxRotSpeed)
    override fun controllerLookAt(wantedX: Double, wantedY: Double, wantedZ: Double, yMaxRotSpeed: Float, xMaxRotAngle: Float) = controllerHandler.controllerLookAt(wantedX, wantedY, wantedZ, yMaxRotSpeed, xMaxRotAngle)
    override fun controllerMoveTo(location: Location) = controllerHandler.controllerMoveTo(location)
    override fun controllerMoveBy(locations: List<Location>, speed: Double, fixHeight: Boolean) = controllerHandler.controllerMoveBy(locations, speed, fixHeight)
    override fun controllerStopMove() = controllerHandler.controllerStopMove()
    override fun random() = controllerHandler.random()

    // ═══════════════════════════════════════════════════════════════════════════════
    // 委托方法 - GenericEntity
    // ═══════════════════════════════════════════════════════════════════════════════

    override var ticksFrozenInPowderedSnow by genericEntityHandler::ticksFrozenInPowderedSnow
    override fun getDisplayName() = genericEntityHandler.getDisplayName()
    override fun isFired() = genericEntityHandler.isFired()
    override fun isSneaking() = genericEntityHandler.isSneaking()
    override fun isSprinting() = genericEntityHandler.isSprinting()
    override fun isSwimming() = genericEntityHandler.isSwimming()
    override fun isInvisible() = genericEntityHandler.isInvisible()
    override fun isGlowing() = genericEntityHandler.isGlowing()
    override fun isFlyingElytra() = genericEntityHandler.isFlyingElytra()
    override fun isNoGravity() = genericEntityHandler.isNoGravity()
    override fun setFired(onFire: Boolean) = genericEntityHandler.setFired(onFire)
    override fun setSneaking(sneaking: Boolean) = genericEntityHandler.setSneaking(sneaking)
    override fun setSprinting(sprinting: Boolean) = genericEntityHandler.setSprinting(sprinting)
    override fun setSwimming(swimming: Boolean) = genericEntityHandler.setSwimming(swimming)
    override fun setInvisible(invisible: Boolean) = genericEntityHandler.setInvisible(invisible)
    override fun setGlowing(glowing: Boolean) = genericEntityHandler.setGlowing(glowing)
    override fun setFlyingElytra(flyingElytra: Boolean) = genericEntityHandler.setFlyingElytra(flyingElytra)
    override fun setNoGravity(noGravity: Boolean) = genericEntityHandler.setNoGravity(noGravity)
    override fun setCustomNameVisible(value: Boolean) = genericEntityHandler.setCustomNameVisible(value)
    override fun isCustomNameVisible() = genericEntityHandler.isCustomNameVisible()
    override fun setCustomName(value: String) = genericEntityHandler.setCustomName(value)
    override fun getCustomName() = genericEntityHandler.getCustomName()
    override fun getCustomNameRaw() = genericEntityHandler.getCustomNameRaw()
    override fun setPose(pose: BukkitPose) = genericEntityHandler.setPose(pose)
    override fun getPose() = genericEntityHandler.getPose()

    // ═══════════════════════════════════════════════════════════════════════════════
    // 委托方法 - Companionable
    // ═══════════════════════════════════════════════════════════════════════════════

    override fun getHost() = companionHandler.getHost()
    override fun getRootHost() = companionHandler.getRootHost()
    override fun setHost(entity: EntityInstance?) = companionHandler.setHost(entity)
    override fun hasHost() = companionHandler.hasHost()
    override fun isCompanion() = companionHandler.isCompanion()
    override fun getCompanions() = companionHandler.getCompanions()
    override fun getAllCompanions() = companionHandler.getAllCompanions()
    override fun addCompanion(vararg entity: EntityInstance) = companionHandler.addCompanion(*entity)
    override fun removeCompanion(vararg entity: EntityInstance) = companionHandler.removeCompanion(*entity)
    override fun clearCompanions() = companionHandler.clearCompanions()
    override fun verifyCompanion() = companionHandler.verifyCompanion()

    // ═══════════════════════════════════════════════════════════════════════════════
    // 委托方法 - Metaable
    // ═══════════════════════════════════════════════════════════════════════════════

    override fun <T> getMetadata(key: String): T = metaHandler.getMetadata(key)
    override fun setMetadata(key: String, value: Any) = metaHandler.setMetadata(key, value)
    override fun getAvailableEntityMeta() = metaHandler.getAvailableEntityMeta()
    override fun updateEntityMetadata() = metaHandler.updateEntityMetadata()
    override fun updateEntityMetadata(viewer: Player) = metaHandler.updateEntityMetadata(viewer)
    override fun generateEntityMetadata(player: Player) = metaHandler.generateEntityMetadata(player)
    fun getByteMaskKey(index: Int) = metaHandler.getByteMaskKey(index)

    // ═══════════════════════════════════════════════════════════════════════════════
    // 委托方法 - TagContainer
    // ═══════════════════════════════════════════════════════════════════════════════

    override fun getTags() = tagHandler.getTags()
    override fun getTag(key: String) = tagHandler.getTag(key)
    override fun hasTag(key: String) = tagHandler.hasTag(key)
    override fun setTag(key: String, value: Any?) = tagHandler.setTag(key, value)
    override fun removeTag(key: String) = tagHandler.removeTag(key)
    override fun getPersistentTags() = tagHandler.getPersistentTags()
    override fun getPersistentTag(key: String) = tagHandler.getPersistentTag(key)
    override fun hasPersistentTag(key: String) = tagHandler.hasPersistentTag(key)
    override fun setPersistentTag(key: String, value: String?) = tagHandler.setPersistentTag(key, value)
    override fun removePersistentTag(key: String) = tagHandler.removePersistentTag(key)

    // ═══════════════════════════════════════════════════════════════════════════════
    // 序列化方法 - EntitySerializable
    // ═══════════════════════════════════════════════════════════════════════════════

    override fun toJson() = serializationHandler.toJson()
    override fun toYaml(transfer: Function<String, String>) = serializationHandler.toYaml(transfer)
    override fun toSection(section: ConfigurationSection, transfer: Function<String, String>) = serializationHandler.toSection(section, transfer)

    // ═══════════════════════════════════════════════════════════════════════════════
    // 其他方法
    // ═══════════════════════════════════════════════════════════════════════════════

    override fun setCustomMeta(key: String, value: String?) = customMetaHandler.setCustomMeta(key, value)
    override fun addAttachEntity(id: Int, relativePos: Vector) { attachedEntity[id] = relativePos.clone() }
    override fun removeAttachEntity(id: Int) { attachedEntity.remove(id) }
    override fun getAttachEntities(): Map<Int, Vector> = attachedEntity

    @Deprecated("请使用 setVelocity(vector)", replaceWith = ReplaceWith("setVelocity(vector)"))
    override fun sendVelocity(vector: Vector) {
        Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityVelocity(getVisiblePlayers(), index, vector)
    }

    override fun sendAnimation(animation: BukkitAnimation) {
        if (this is ModelEngine && animation == BukkitAnimation.TAKE_DAMAGE && modelEngineName.isNotBlank()) {
            hurt()
        } else {
            if (animation == BukkitAnimation.TAKE_DAMAGE && MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_19)) {
                sendHurtAnimation(0f)
            } else {
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityAnimation(getVisiblePlayers(), index, animation)
            }
        }
    }

    override fun sendHurtAnimation(yaw: Float) {
        Adyeshach.api().getMinecraftAPI().getEntityOperator().updateHurtAnimation(getVisiblePlayers(), index, yaw)
    }

    /**
     * 更新可见实体索引
     */
    fun updateVisibleEntityIndex(player: Player, visible: Boolean) {
        val finder = Adyeshach.api().getEntityFinder()
        if (visible) {
            finder.addVisibleEntity(player, this)
        } else {
            finder.removeVisibleEntity(player, this)
        }
    }

    fun registerClientEntity(viewer: Player) {
        if (useClientEntityMap) {
            val map = clientEntityMap.getOrCreate(viewer) { ConcurrentHashMap() } ?: return
            map[index] = ClientEntity(this)
        }
    }

    fun unregisterClientEntity(viewer: Player) {
        if (useClientEntityMap) {
            clientEntityMap[viewer]?.remove(index)
        }
    }
}
