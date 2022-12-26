package ink.ptms.adyeshach.impl.entity

import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.bukkit.BukkitAnimation
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
import ink.ptms.adyeshach.core.util.modify
import ink.ptms.adyeshach.impl.util.Indexs
import ink.ptms.adyeshach.impl.util.ifTrue
import org.bukkit.Location
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
abstract class DefaultEntityInstance(entityType: EntityTypes) :
    DefaultEntityBase(entityType), EntityInstance, DefaultControllable, DefaultGenericEntity, DefaultRideable, DefaultViewable, InternalEntity, TickService {

    /** 实体序号 */
    override val index: Int = Indexs.nextIndex()

    /** 可见玩家 */
    override val viewPlayers = DefaultViewPlayers(this)

    /** 是否移除 */
    override var isRemoved = false

    /** 是否傻子 */
    @Expose
    override var isNitwit = false

    /** 移动速度 */
    @Expose
    override var moveSpeed = 0.2

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
    var brain = SimpleBrain(this)

    /** 客户端位置 */
    var clientPosition = position
        set(value) {
            field = value.clone()
        }

    /** 客户端位置修正 */
    var clientPositionFixed = System.currentTimeMillis()

    /** 客户端更新间隔 */
    var clientUpdateInterval = Baffle.of(Adyeshach.api().getEntityTypeRegistry().getEntityClientUpdateInterval(entityType))

    /** 单位移动量 */
    var deltaMovement = Vector(0.0, 0.0, 0.0)
        set(value) {
            field = value.clone()
        }

    /** 载具位置同步 */
    var vehicleSync = System.currentTimeMillis()

    /** 插值定位 */
    var moveFrames: InterpolatedLocation? = null

    /** 移动目的 */
    override var moveTarget: Location? = null
        set(value) {
            field = value
            // 更新移动路径
            updateMoveFrames()
        }

    /** 实体路径类型 */
    val pathType = Adyeshach.api().getEntityTypeRegistry().getEntityPathType(entityType)

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

    override fun setCustomMeta(key: String, value: String): Boolean {
        super.setMetadata(key, value).ifTrue { return true }
        return when (key) {
            "nitwit" -> {
                isNitwit = value.cbool
                true
            }
            "movespeed", "move_speed" -> {
                moveSpeed = value.cdouble
                true
            }
            "visibledistance", "visible_distance" -> {
                visibleDistance = value.cdouble
                true
            }
            "visibleafterloaded", "visible_after_loaded" -> {
                visibleAfterLoaded = value.cbool
                true
            }
            "modelenginename", "modelengine_name", "modelengine" -> {
                modelEngineName = value
                true
            }
            else -> false
        }
    }

    override fun prepareSpawn(viewer: Player, spawn: Runnable): Boolean {
        if (AdyeshachEntityVisibleEvent(this, viewer, true).call()) {
            // 若未生成 ModelEngine 模型则发送原版数据包
            // 这可能会导致 getEntityFromClientUniqueId 方法无法获取
            if (this !is ModelEngine || !showModelEngine(viewer)) {
                // 调用生成方法
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
            // 销毁模型
            if (this !is ModelEngine || !hideModelEngine(viewer)) {
                // 调用销毁方法
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
        spawn(position.toLocation())
    }

    override fun despawn(destroyPacket: Boolean, removeFromManager: Boolean) {
        if (destroyPacket) {
            forViewers { visible(it, false) }
            AdyeshachEntityDestroyEvent(this).call()
        }
        if (removeFromManager) {
            if (manager != null) {
                isRemoved = true
                manager!!.delete(this)
                manager = null
                AdyeshachEntityRemoveEvent(this).call()
            }
        }
    }

    override fun teleport(entityPosition: EntityPosition) {
        teleport(entityPosition.toLocation())
    }

    override fun teleport(x: Double, y: Double, z: Double) {
        teleport(position.toLocation().modify(x, y, z))
    }

    override fun teleport(location: Location) {
        // 处理事件
        val eventBus = getEventBus()
        if (eventBus != null && !eventBus.callTeleport(this, location)) {
            return
        }
        val newPosition = EntityPosition.fromLocation(location)
        // 是否发生位置变更
        if (newPosition == position) {
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
        val eventBus = getEventBus()
        if (eventBus == null || eventBus.callVelocity(this, vector)) {
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
        position.toLocation().add(0.0, size.height * 0.9, 0.0).also { entityLocation ->
            entityLocation.direction = location.clone().subtract(entityLocation).toVector()
            setHeadRotation(entityLocation.yaw, entityLocation.pitch, forceUpdate)
        }
    }

    override fun setHeadRotation(yaw: Float, pitch: Float, forceUpdate: Boolean) {
        // 强制更新
        if (forceUpdate) {
            Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityLook(getVisiblePlayers(), index, yaw, pitch, !pathType.isFly())
        } else {
            teleport(position.toLocation().modify(yaw, pitch))
        }
    }

    override fun sendAnimation(animation: BukkitAnimation) {
        if (this is ModelEngine && animation == BukkitAnimation.TAKE_DAMAGE) {
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
            // 更新位置
            syncPosition()
        }
    }

    override fun clone(newId: String, location: Location, manager: Manager?): EntityInstance? {
        val json = JsonParser().parse(toJson()).asJsonObject
        json.addProperty("id", newId)
        json.addProperty("uniqueId", UUID.randomUUID().toString().replace("-", ""))
        val entity = Adyeshach.api().getEntitySerializer().fromJson(json.toString()) ?: return null
        (manager ?: this.manager)?.add(entity)
        entity as DefaultEntityInstance
        entity.tag.putAll(tag)
        entity.persistentTag.putAll(persistentTag)
        entity.manager = (manager ?: this.manager)
        entity.position = EntityPosition.fromLocation(location)
        entity.passengers.clear()
        // 复制观察者
        forViewers { entity.addViewer(it) }
        // 复制关联单位
        getPassengers().forEachIndexed { i, p ->
            p.clone("${newId}_passenger_$i", location)?.let { entity.addPassenger(it) }
        }
        return entity
    }

    @Deprecated("请使用 setVelocity(vector)", replaceWith = ReplaceWith("setVelocity(vector)"))
    override fun sendVelocity(vector: Vector) {
        Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityVelocity(getVisiblePlayers(), index, vector)
    }
}