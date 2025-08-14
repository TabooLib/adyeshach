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
import ink.ptms.adyeshach.core.entity.manager.PlayerManager
import ink.ptms.adyeshach.core.entity.path.InterpolatedLocation
import ink.ptms.adyeshach.core.entity.path.PathFinderHandler
import ink.ptms.adyeshach.core.entity.path.ResultNavigation
import ink.ptms.adyeshach.core.event.*
import ink.ptms.adyeshach.core.util.*
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.ServerTours
import ink.ptms.adyeshach.impl.VisualTeam
import ink.ptms.adyeshach.impl.entity.controller.BionicSight
import ink.ptms.adyeshach.impl.manager.DefaultManagerHandler.playersInGameTick
import ink.ptms.adyeshach.impl.util.ChunkAccess
import ink.ptms.adyeshach.impl.util.Indexs
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit
import taboolib.common5.Baffle
import taboolib.common5.cbool
import taboolib.common5.cdouble
import taboolib.common5.clong
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
 *
 * @author 坏黑
 * @since 2022/6/19 21:26
 */
@Suppress("LeakingThis", "SpellCheckingInspection")
abstract class DefaultEntityInstance(entityType: EntityTypes = EntityTypes.ZOMBIE) :
    DefaultEntityBase(entityType), EntityInstance, DefaultControllable, DefaultGenericEntity, DefaultRideable, DefaultViewable, InternalEntity, TickService {

    override val x: Double
        get() = clientPosition.x

    override val y: Double
        get() = clientPosition.y

    override val z: Double
        get() = clientPosition.z

    override val chunkX: Int
        get() = (x / 16).toInt()

    override val chunkZ: Int
        get() = (z / 16).toInt()

    override val yaw: Float
        get() = clientPosition.yaw

    override val pitch: Float
        get() = clientPosition.pitch

    override val world: World
        get() = clientPosition.world

    /** 实体序号 */
    override val index: Int = Indexs.nextIndex()

    /** 可见玩家 */
    override val viewPlayers = DefaultViewPlayers(this)

    /** 实体大小 */
    override val entitySize = Adyeshach.api().getEntityTypeRegistry().getEntitySize(entityType)

    /** 实体路径类型 */
    override val entityPathType = Adyeshach.api().getEntityTypeRegistry().getEntityPathType(entityType)

    /** 是否移除 */
    override var isRemoved = false

    /** 是否傻子 */
    @Expose
    override var isNitwit = false

    /** 移动速度 */
    @Expose
    override var moveSpeed = 0.2

    /** 是否可见名称 */
    @Expose
    override var isNameTagVisible = true
        set(value) {
            // 与 canSeeInvisible 冲突
            if (!value) {
                canSeeInvisible = false
            }
            field = value
            VisualTeam.updateTeam(this)
        }

    /** 是否碰撞 */
    @Expose
    override var isCollision = true
        set(value) {
            // 与 canSeeInvisible 冲突
            if (!value) {
                canSeeInvisible = false
            }
            field = value
            VisualTeam.updateTeam(this)
        }

    /** 发光颜色 */
    @Expose
    override var glowingColor = ChatColor.WHITE
        set(value) {
            // 与 canSeeInvisible 冲突
            if (value != ChatColor.WHITE) {
                canSeeInvisible = true
            }
            field = value
            VisualTeam.updateTeam(this)
        }

    /** 是否可见隐形单位 */
    @Expose
    override var canSeeInvisible = false
        set(value) {
            // 与其他其他记分板功能冲突
            if (!isNameTagVisible || !isCollision || glowingColor != ChatColor.WHITE) {
                return
            }
            field = value
            VisualTeam.updateTeam(this)
        }

    /** 可视距离 */
    @Expose
    override var visibleDistance = -1.0
        get() = if (field == -1.0) AdyeshachSettings.visibleDistance else field

    /** 加载后自动显示 */
    @Expose
    override var visibleAfterLoaded = true
        set(value) {
            if (!isPublic()) {
                errorBy("error-cannot-set-visible-after-loaded-for-private-entity")
            }
            if (!value) {
                clearViewer()
            }
            field = value
        }

    /** ModelEngine 唯一序号 */
    var modelEngineUniqueId: UUID? = null

    /** ModelEngine 支持 */
    @Expose
    var modelEngineName = ""
        set(value) {
            field = value
            // 重新加载模型
            if (this is ModelEngine) {
                refreshModelEngine()
            }
        }

    /** ModelEngine 配置 */
    var modelEngineOptions: ModelEngineOptions? = null

    /** 骑乘者 */
    @Expose
    var passengers = ConcurrentSkipListSet<String>()

    /** 控制器 */
    @Expose
    var controller = ConcurrentSkipListSet(Comparator.comparing(Controller::id))

    /** Ady 的小脑 */
    override var brain: Brain = SimpleBrain(this)

    /** 仿生视线 */
    var bionicSight = BionicSight(this)

    /** 客户端位置 */
    @Expose
    var clientPosition = position
        set(value) {
            field = value.clone()
        }
        get() {
            // 修正因反序列化带来的坐标偏移
            if (field.isZero() && field != position) {
                field = position
            }
            return field
        }

    /** 客户端位置修正 */
    override var clientPositionFixed = System.currentTimeMillis()

    /** 客户端更新间隔 */
    override var clientPositionUpdateInterval = Baffle.of(Adyeshach.api().getEntityTypeRegistry().getEntityClientUpdateInterval(entityType))

    /** 是否启用客户端更新间隔 **/
    override var isIgnoredClientPositionUpdateInterval = true

    /** 单位移动量 */
    var deltaMovement = Vector(0.0, 0.0, 0.0)
        set(value) {
            field = value.clone()
        }

    override var useClientEntityMap = true

    override var isRotationFixOnSpawn = true

    override var isPassengerRefreshOnSpawn = true

    override var isDisableVisibleEvent = false

    override var isDisableVehicleCheckOnTick = false

    /** 插值定位 */
    override var moveFrames: InterpolatedLocation? = null
        set(value) {
            field = value
            DefaultAdyeshachAPI.localEventBus.callMove(this, value != null)
        }

    /** 移动目的 */
    override var moveTarget: Location? = null
        set(value) {
            field = value
            // 更新移动路径
            // 傻子或没有目的地
            if (isNitwit || moveTarget == null) {
                // 移除移动路径
                if (moveFrames != null) {
                    moveFrames = null
                    // 移除移动标签
                    tag.remove(StandardTags.IS_MOVING)
                }
                return
            }
            // 设置标签
            setTag(StandardTags.IS_PATHFINDING, true)
            // 请求路径
            PathFinderHandler.request(position.toLocation(), moveTarget!!, entityPathType) {
                it as ResultNavigation
                // 移除标签
                removeTag(StandardTags.IS_PATHFINDING)
                // 按照路径移动
                controllerMoveBy(it.pointList.map { v -> v.toLocation(world) })
            }
        }

    /** 管理器 */
    override var manager: Manager? = null
        set(value) {
            // 没有管理器 || 移除管理器
            if (field == null || value == null) {
                field = value
                // 更新标签
                // 孤立单位
                if (value == null || value !is TickService) {
                    tag[StandardTags.ISOLATED] = true
                } else {
                    tag.remove(StandardTags.ISOLATED)
                    // 公共单位
                    if (value.isPublic()) {
                        tag[StandardTags.IS_PUBLIC] = true
                        tag.remove(StandardTags.IS_PRIVATE)
                    } else {
                        tag[StandardTags.IS_PRIVATE] = true
                        tag.remove(StandardTags.IS_PUBLIC)
                    }
                    // 临时单位
                    if (value.isTemporary()) {
                        tag[StandardTags.IS_TEMPORARY] = true
                    } else {
                        tag.remove(StandardTags.IS_TEMPORARY)
                    }
                }
            } else {
                errorBy("error-manager-has-been-initialized")
            }
        }

    /** 附着单位 */
    val attachedEntity = ConcurrentHashMap<Int, Vector>()

    // 缓存的载具实体
    var cacheVehicleEntity: EntityInstance? = null

    override fun setCustomMeta(key: String, value: String?): Boolean {
        // region setCustomMeta
        return when (key) {
            // 实体姿态
            "pose" -> {
                setPose(if (value != null) BukkitPose::class.java.getEnum(value) else BukkitPose.STANDING)
                true
            }
            // 是否傻逼
            "nitwit" -> {
                isNitwit = value?.cbool ?: false
                true
            }
            // 移动速度
            "movespeed", "move_speed" -> {
                moveSpeed = value?.cdouble ?: 0.2
                true
            }
            // 是否可见名称
            "nametagvisible", "name_tag_visible" -> {
                isNameTagVisible = value?.cbool ?: true
                true
            }
            // 是否存在碰撞体积
            "collision", "is_collision" -> {
                isCollision = value?.cbool ?: true
                true
            }
            // 发光颜色
            "glowingcolor", "glowing_color" -> {
                glowingColor = if (value != null) {
                    if (value.startsWith('§')) {
                        ChatColor.getByChar(value) ?: ChatColor.WHITE
                    } else {
                        ChatColor::class.java.getEnum(value)
                    }
                } else {
                    ChatColor.WHITE
                }
                true
            }
            // 是否可见隐形单位
            "canseeinvisible", "can_see_invisible" -> {
                canSeeInvisible = value?.cbool ?: false
                true
            }
            // 可见距离
            "visibledistance", "visible_distance" -> {
                visibleDistance = value?.cdouble ?: AdyeshachSettings.visibleDistance
                true
            }
            // 加载后自动显示
            "visibleafterloaded", "visible_after_loaded" -> {
                visibleAfterLoaded = value?.cbool ?: true
                true
            }
            // 模型引擎
            "modelenginename", "modelengine_name", "modelengine", "model_engine" -> {
                modelEngineName = value ?: ""
                true
            }
            // 冻结
            "freeze", "isfreeze", "is_freeze" -> {
                isFreeze = value?.cbool ?: false
                true
            }
            // 其他
            else -> false
        }
        // endregion
    }

    override fun prepareSpawn(viewer: Player, spawn: Runnable): Boolean {
        if (isDisableVisibleEvent || AdyeshachEntityVisibleEvent(this, viewer, true).call()) {
            // 使用事件系统控制实体显示
            if (DefaultAdyeshachAPI.localEventBus.callSpawn(this, viewer)) {
                spawn.run()
            }
            DefaultAdyeshachAPI.localEventBus.postSpawn(this, viewer)
            // 更新单位属性
            updateEntityMetadata(viewer)
            // 更新单位视角
            if (isRotationFixOnSpawn) {
                setHeadRotation(position.yaw, position.pitch, forceUpdate = true)
            }
            // 关联实体初始化
            if (isPassengerRefreshOnSpawn) {
                submit(delay = 2) { refreshPassenger(viewer) }
            }
            return true
        }
        return false
    }

    override fun prepareDestroy(viewer: Player, destroy: Runnable): Boolean {
        if (isDisableVisibleEvent || AdyeshachEntityVisibleEvent(this, viewer, false).call()) {
            // 使用事件系统控制实体销毁
            if (DefaultAdyeshachAPI.localEventBus.callDestroy(this, viewer)) {
                destroy.run()
                DefaultAdyeshachAPI.localEventBus.postDestroy(this, viewer)
            }
            return true
        }
        return false
    }

    override fun isPublic(): Boolean {
        return manager?.isPublic() == true
    }

    override fun isTemporary(): Boolean {
        return manager?.isTemporary() == true
    }

    override fun spawn(location: Location) {
        position = EntityPosition.fromLocation(location)
        clientPosition = position
        forViewers { visible(it, true) }
        AdyeshachEntitySpawnEvent(this).call()
    }

    override fun respawn() {
        if (isRemoved) {
            error("Entity has been removed")
        }
        spawn(clientPosition.toLocation())
    }

    override fun despawn(destroyPacket: Boolean, removeFromManager: Boolean) {
        if (destroyPacket) {
            forViewers { visible(it, false) }
            AdyeshachEntityDestroyEvent(this).call()
        }
        if (removeFromManager) {
            if (manager != null) {
                isRemoved = true
                manager!!.remove(this)
                AdyeshachEntityRemoveEvent(this).call()
                manager = null
            }
        }
    }

    override fun teleport(entityPosition: EntityPosition) {
        teleport(entityPosition.toLocation())
    }

    override fun teleport(x: Double, y: Double, z: Double) {
        teleport(clientPosition.toLocation().modify(x, y, z))
    }

    override fun teleport(location: Location) {
        // 异常角度警告
        if (location.yaw.isNaN() || location.pitch.isNaN()) {
            IllegalStateException("Invalid head rotation (yaw=${location.yaw}, pitch=${location.pitch})").printStackTrace()
        }
        // 处理事件
        val eventBus = DefaultAdyeshachAPI.localEventBus
        if (eventBus.callTeleport(this, location)) {
            eventBus.postTeleport(this, location)
        } else {
            return
        }
        val newPosition = EntityPosition.fromLocation(location)
        // 强制传送
        if (tag.containsKey(StandardTags.FORCE_TELEPORT)) {
            tag.remove(StandardTags.FORCE_TELEPORT)
        }
        // 如果坐标没变则不做处理
        else if (newPosition == position) {
            return
        }
        // 是否发生实质性位置变更
        val isMoved = position.x != newPosition.x || position.y != newPosition.y || position.z != newPosition.z
        // 是否切换世界
        if (position.world != newPosition.world) {
            position = newPosition
            despawn()
            respawn()
        }
        // 无管理器 || 孤立管理器 || 不允许进行位置同步
        if (manager == null || manager !is TickService || !allowSyncPosition()) {
            position = newPosition
            clientPosition = position
            Adyeshach.api().getMinecraftAPI().getEntityOperator().teleportEntity(
                getVisiblePlayers(),
                index,
                location.modify(yaw = entityType.fixYaw(location.yaw))
            )
        } else {
            clientPosition = newPosition
        }
        // 只有在位置发生变更时才进行 passengers 同步
        if (isMoved) {
            // 同步 passengers 位置
            getPassengers().forEach { it.teleport(location) }
            // 更新 passengers 信息
            refreshPassenger()
        }
    }

    override fun setVelocity(vector: Vector) {
        val eventBus = DefaultAdyeshachAPI.localEventBus
        if (eventBus.callVelocity(this, vector)) {
            deltaMovement = vector.clone()
        }
    }

    override fun setVelocity(x: Double, y: Double, z: Double) {
        setVelocity(Vector(x, y, z))
    }

    override fun getVelocity(): Vector {
        return deltaMovement.clone()
    }

    override fun setHeadRotation(location: Location, forceUpdate: Boolean) {
        val size = Adyeshach.api().getEntityTypeRegistry().getEntitySize(entityType)
        clientPosition.toLocation().add(0.0, size.height * 0.9, 0.0).also { entityLocation ->
            entityLocation.direction = location.clone().subtract(entityLocation).toVector()
            setHeadRotation(entityLocation.yaw, entityLocation.pitch, forceUpdate)
        }
    }

    override fun setHeadRotation(yaw: Float, pitch: Float, forceUpdate: Boolean) {
        if (AdyeshachEntityHeadRotationEvent(this, yaw, pitch, forceUpdate).call()) {
            // 强制更新
            if (forceUpdate) {
                position.yaw = yaw
                position.pitch = pitch
                clientPosition.yaw = yaw
                clientPosition.pitch = pitch
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityLook(
                    getVisiblePlayers(),
                    index,
                    entityType.fixYaw(yaw),
                    pitch,
                    !entityPathType.isFly()
                )
            } else {
                teleport(clientPosition.toLocation().modify(yaw, pitch))
            }
        }
    }

    override fun sendAnimation(animation: BukkitAnimation) {
        if (this is ModelEngine && animation == BukkitAnimation.TAKE_DAMAGE && modelEngineName.isNotBlank()) {
            hurt()
        } else {
            Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityAnimation(getVisiblePlayers(), index, animation)
        }
    }

    override fun addAttachEntity(id: Int, relativePos: Vector) {
        attachedEntity[id] = relativePos.clone()
    }

    override fun removeAttachEntity(id: Int) {
        attachedEntity.remove(id)
    }

    override fun getAttachEntities(): Map<Int, Vector> {
        return attachedEntity
    }

    override fun onTick() {
        // 处理玩家可见
        // 大量用户反馈的 NPC 概率性不可见问题，根本原因在于这个逻辑写垃圾
        // 尝试性修复 - 2023/12/29: 玩家在可见范围内呆上一个检查周期后才会显示实体，并缩短检查周期 (5s -> 2s)
        // 尝试性修复 - 2024/02/27: 基于原版 PlayerChunkMap 的区块可见性决定实体可见性
        if (viewPlayers.visibleRefreshLocker.hasNext()) {
            // 同步到载具位置
            if (!isDisableVehicleCheckOnTick) {
                val vehicle = cacheVehicleEntity
                if (vehicle != null) {
                    position = vehicle.position.copy(yaw = position.yaw, pitch = position.pitch)
                    clientPosition = vehicle.position.copy(yaw = clientPosition.yaw, pitch = clientPosition.pitch)
                    setPersistentTag(StandardTags.IS_IN_VEHICLE, "true")
                } else {
                    removePersistentTag(StandardTags.IS_IN_VEHICLE)
                }
            }
            // 同步可见状态
            val entityManager = manager
            if (entityManager is PlayerManager) {
                handleVisible(entityManager.owner)
            } else {
                playersInGameTick.forEach { handleVisible(it) }
            }
        }
        // 允许位置同步
        if (allowSyncPosition()) {
            // 处理移动
            handleMove()
            // 处理行为
            brain.tick()
            bionicSight.tick()
            // 更新位置
            syncPosition()
        }
    }

    private fun allowSyncPosition(): Boolean {
        // 不是傻子 && 存在可见玩家 && 所在区块已经加载
        return !isNitwit && viewPlayers.hasVisiblePlayer() && ChunkAccess.getChunkAccess(world).isChunkLoaded(chunkX, chunkZ)
    }

    // 更新可见性
    private fun handleVisible(player: Player) {
        // 是观察者
        if (player.name in viewPlayers.viewers) {
            // 是可见的观察者
            if (player.name in viewPlayers.visible) {
                // 销毁不在可视范围内的实体
                if (!isInVisibleDistance(player) && !ServerTours.isRoutePlaying(player)) {
                    visible(player, false)
                }
            } else {
                // 属否在可视范围内 && 所在区块是否可见 && 显示实体
                if (isInVisibleDistance(player) && Adyeshach.api().getMinecraftAPI().getHelper().isChunkVisible(player, chunkX, chunkZ)) {
                    visible(player, true)
                }
            }
        }
    }

    // 处理移动
    private fun handleMove() {
        // region handleMove
        // 乘坐实体 || 冻结
        if (hasTag(StandardTags.IS_IN_VEHICLE) || hasTag(StandardTags.IS_FROZEN)) {
            deltaMovement = Vector(0.0, 0.0, 0.0)
            return
        }
        // 行走
        if (moveFrames != null) {
            // 是否已抵达目的地
            if (moveFrames!!.isArrived()) {
                // 同步朝向
                moveTarget?.let { setHeadRotation(it.yaw, it.pitch, true) }
                moveTarget = null
                return
            }
            // 首次移动
            // 在单位首次移动之前，会有 0.25 秒的时间用于调整视角
            // 在这期间，单位会保持原地不动，并持有 "IS_MOVING_START" 标签
            if (!tag.containsKey(StandardTags.IS_MOVING)) {
                var cur = 1
                var next = moveFrames!!.peek(cur)
                while (next != null && x == next.x && y == next.y && z == next.z) {
                    cur++
                    next = moveFrames!!.peek(cur)
                }
                if (next != null && (tag[StandardTags.IS_MOVING_START] == null || tag[StandardTags.IS_MOVING_START].clong > System.currentTimeMillis())) {
                    // 初始化等待时间
                    if (tag[StandardTags.IS_MOVING_START] == null) {
                        tag[StandardTags.IS_MOVING_START] = System.currentTimeMillis() + 250
                    }
                    // 调整视角
                    controllerLookAt(next.x, getEyeLocation().y, next.z, 35f, 40f)
                    return
                }
            }
            // 正在移动视角
            if (bionicSight.isLooking) {
                return
            }
            tag.remove(StandardTags.IS_MOVING_START)
            // 获取下一个移动点
            val next = moveFrames!!.next()
            if (next != null) {
                // 设置移动标签
                tag[StandardTags.IS_MOVING] = true
                // 默认会看向移动方向
                val eyeLocation = clientPosition.toLocation().plus(y = entitySize.height * 0.9)
                // 设置方向
                eyeLocation.direction = Vector(next.x, eyeLocation.y, next.z).subtract(eyeLocation.toVector())
                // 不会看向脚下
                if (eyeLocation.pitch < 90f) {
                    next.yaw = EntityPosition.normalizeYaw(eyeLocation.yaw)
                    next.pitch = EntityPosition.normalizePitch(eyeLocation.pitch)
                }
                // 更新位置
                if (next.yaw.isNaN() || next.pitch.isNaN()) {
                    teleport(next.x, next.y, next.z)
                } else {
                    teleport(next)
                }
                // 调试模式下显示路径
                if (AdyeshachSettings.debug) {
                    world.spawnParticle(org.bukkit.Particle.VILLAGER_HAPPY, next.x, next.y, next.z, 2, 0.0, 0.0, 0.0, 0.0)
                }
            }
        }
        // 是否处于活动状态
        if (deltaMovement.lengthSquared() > 1E-6) {
            // 获取下一个移动位置
            val nextPosition = clientPosition.clone().add(deltaMovement.x, deltaMovement.y, deltaMovement.z)
            // 只有在向下移动的时候才会进行碰撞检测
            if (deltaMovement.y < 0) {
                val chunkAccess = ChunkAccess.getChunkAccess(world)
                val blockHeight = chunkAccess.getBlockTypeAndHeight(nextPosition.x, nextPosition.y, nextPosition.z)
                if (blockHeight.first.isSolid) {
                    clientPosition = nextPosition
                    clientPosition.y = ifloor(nextPosition.y) + blockHeight.second + 0.01
                    deltaMovement = Vector(0.0, 0.0, 0.0)
                    return
                }
            }
            // 更新位置
            clientPosition = nextPosition
            // 更新速度
            deltaMovement = Vector(deltaMovement.x * 0.9, (deltaMovement.y - 0.08) * 0.98, deltaMovement.z * 0.9)
        }
        // endregion
    }

    // 同步位置
    private fun syncPosition() {
        // region syncPosition
        val updateRotation = (yaw - position.yaw).absoluteValue >= 1 || (pitch - position.pitch).absoluteValue >= 1 || taboolib.common.util.random(0.2)
        val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
        // 乘坐实体
        if (hasPersistentTag(StandardTags.IS_IN_VEHICLE)) {
            // 是否需要更新视角
            if (updateRotation) {
                operator.updateEntityLook(getVisiblePlayers(), index, entityType.fixYaw(yaw), pitch, true)
            }
        } else {
            // 是否需要更新位置
            if (clientPosition != position) {
                // 计算差值
                val offset = clientPosition.clone().subtract(position)
                val x = encodePos(offset.x)
                val y = encodePos(offset.y)
                val z = encodePos(offset.z)
                val requireTeleport = x < -32768L || x > 32767L || y < -32768L || y > 32767L || z < -32768L || z > 32767L
                if (requireTeleport || clientPositionFixed + TimeUnit.SECONDS.toMillis(20) < System.currentTimeMillis()) {
                    clientPositionFixed = System.currentTimeMillis()
                    val toLocation = clientPosition.toLocation().modify(yaw = entityType.fixYaw(clientPosition.yaw))
                    operator.teleportEntity(getVisiblePlayers(), index, toLocation, !entityPathType.isFly())
                    position = clientPosition
                } else {
                    val updatePosition = offset.lengthSquared() > 1E-6
                    if (updatePosition) {
                        // 更新间隔
                        if (isIgnoredClientPositionUpdateInterval || clientPositionUpdateInterval.hasNext()) {
                            if (updateRotation) {
                                operator.updateRelEntityMoveLook(
                                    getVisiblePlayers(),
                                    index,
                                    x.toShort(),
                                    y.toShort(),
                                    z.toShort(),
                                    entityType.fixYaw(yaw),
                                    pitch,
                                    !entityPathType.isFly()
                                )
                            } else {
                                operator.updateRelEntityMove(
                                    getVisiblePlayers(),
                                    index,
                                    x.toShort(),
                                    y.toShort(),
                                    z.toShort(),
                                    !entityPathType.isFly()
                                )
                            }
                            position = clientPosition
                        }
                    } else {
                        operator.updateEntityLook(getVisiblePlayers(), index, entityType.fixYaw(yaw), pitch, !entityPathType.isFly())
                        position = clientPosition
                    }
                }
            }
        }
        // endregion
    }

    override fun clone(newId: String, location: Location, manager: Manager?): EntityInstance? {
        val json = JsonParser().parse(toJson()).asJsonObject
        json.addProperty("id", newId)
        json.addProperty("uniqueId", UUID.randomUUID().toString().replace("-", ""))
        val entity = Adyeshach.api().getEntitySerializer().fromJson(json.toString())
        entity as DefaultEntityInstance
        entity.tag.putAll(tag)
        entity.persistentTag.putAll(persistentTag)
        entity.manager = (manager ?: this.manager)
        entity.position = EntityPosition.fromLocation(location)
        entity.clientPosition = entity.position
        entity.passengers.clear()
        // 复制观察者
        forViewers { entity.addViewer(it) }
        // 复制关联单位
        getPassengers().forEachIndexed { i, p ->
            p.clone("${newId}_passenger_$i", location)?.let { entity.addPassenger(it) }
        }
        // 添加到管理器
        entity.manager?.add(entity)
        return entity
    }

    override fun setDerived(id: String) {
        isNitwit = true
        setPersistentTag(StandardTags.DERIVED, id)
    }

    @Deprecated("请使用 setVelocity(vector)", replaceWith = ReplaceWith("setVelocity(vector)"))
    override fun sendVelocity(vector: Vector) {
        Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityVelocity(getVisiblePlayers(), index, vector)
    }

    override fun refreshPosition() {
        val location = getLocation()
        Adyeshach.api().getMinecraftAPI().getEntityOperator().teleportEntity(
            getVisiblePlayers(),
            index,
            location.modify(yaw = entityType.fixYaw(location.yaw))
        )
    }

    override fun getLocation(): Location {
        return clientPosition.toLocation()
    }

    override fun getEyeLocation(): Location {
        return clientPosition.toLocation().plus(y = entitySize.height)
    }
}