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
import ink.ptms.adyeshach.core.event.AdyeshachEntityDestroyEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntitySpawnEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.core.util.getEnum
import ink.ptms.adyeshach.core.util.modify
import ink.ptms.adyeshach.core.util.plus
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.VisualTeam
import ink.ptms.adyeshach.impl.entity.controller.BionicSight
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
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

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

    /** 骑乘者 */
    @Expose
    var passengers = CopyOnWriteArraySet<String>()

    /** 控制器 */
    @Expose
    var controller = CopyOnWriteArrayList<Controller>()

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

    /** 载具位置同步 */
    override var vehicleSync = System.currentTimeMillis()

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
            updateMoveFrames()
        }

    /** 管理器 */
    override var manager: Manager? = null
        set(value) {
            // 没有管理器 || 移除管理器
            if (field == null || value == null) {
                field = value
                // 更新标签
                updateManagerTags()
            } else {
                errorBy("error-manager-has-been-initialized")
            }
        }

    override fun setCustomMeta(key: String, value: String?): Boolean {
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
    }

    override fun prepareSpawn(viewer: Player, spawn: Runnable): Boolean {
        if (AdyeshachEntityVisibleEvent(this, viewer, true).call()) {
            // 使用事件系统控制实体显示
            if (DefaultAdyeshachAPI.localEventBus.callSpawn(this, viewer)) {
                spawn.run()
            }
            // 更新单位属性
            updateEntityMetadata(viewer)
            // 更新单位视角
            setHeadRotation(position.yaw, position.pitch, forceUpdate = true)
            // 关联实体初始化
            submit(delay = 5) { refreshPassenger(viewer) }
            return true
        }
        return false
    }

    override fun prepareDestroy(viewer: Player, destroy: Runnable): Boolean {
        if (AdyeshachEntityVisibleEvent(this, viewer, false).call()) {
            // 使用事件系统控制实体销毁
            if (DefaultAdyeshachAPI.localEventBus.callDestroy(this, viewer)) {
                destroy.run()
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
                manager = null
                AdyeshachEntityRemoveEvent(this).call()
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
        // 是否发生位置变更
        else if (newPosition == position) {
            return
        }
        // 切换世界
        if (position.world != newPosition.world) {
            position = newPosition
            despawn()
            respawn()
        }
        // 无管理器 || 孤立管理器 || 不允许进行位置同步
        if (manager == null || manager !is TickService || !allowSyncPosition()) {
            position = newPosition
            clientPosition = position
            Adyeshach.api().getMinecraftAPI().getEntityOperator().teleportEntity(getVisiblePlayers(), index, location)
        } else {
            clientPosition = newPosition
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
        // 强制更新
        if (forceUpdate) {
            position.yaw = yaw
            position.pitch = pitch
            clientPosition.yaw = yaw
            clientPosition.pitch = pitch
            Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityLook(getVisiblePlayers(), index, yaw, pitch, !entityPathType.isFly())
        } else {
            teleport(clientPosition.toLocation().modify(yaw, pitch))
        }
    }

    override fun sendAnimation(animation: BukkitAnimation) {
        if (this is ModelEngine && animation == BukkitAnimation.TAKE_DAMAGE && modelEngineName.isNotBlank()) {
            hurt()
        } else {
            Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityAnimation(getVisiblePlayers(), index, animation)
        }
    }

    override fun onTick() {
        // 处理玩家可见
        handleTracker()
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
        Adyeshach.api().getMinecraftAPI().getEntityOperator().teleportEntity(getVisiblePlayers(), index, getLocation())
    }

    override fun getLocation(): Location {
        return clientPosition.toLocation()
    }

    override fun getEyeLocation(): Location {
        return clientPosition.toLocation().plus(y = entitySize.height)
    }
}