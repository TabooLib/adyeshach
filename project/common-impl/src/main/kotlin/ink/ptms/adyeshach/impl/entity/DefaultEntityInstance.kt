package ink.ptms.adyeshach.impl.entity

import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.AdyeshachSettings
import ink.ptms.adyeshach.api.event.*
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import ink.ptms.adyeshach.common.entity.*
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.util.Indexs
import ink.ptms.adyeshach.common.util.errorBy
import ink.ptms.adyeshach.common.util.plugin.CompatServerTours
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.world.WorldUnloadEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
 *
 * @author 坏黑
 * @since 2022/6/19 21:26
 */
@Suppress("LeakingThis")
abstract class DefaultEntityInstance(entityType: EntityTypes) :
    DefaultEntityBase(entityType), EntityInstance, DefaultControllable, DefaultGenericEntity, DefaultRideable, DefaultViewable, InternalEntity, TickService {

    /** 实体序号 */
    override val index: Int = Indexs.nextIndex()

    /** 可见玩家 */
    override val viewPlayers = DefaultViewPlayers(this)

    /** 移动速度 */
    @Expose
    override var moveSpeed = 0.2

    /** Ady 的小脑 */
    var brain = SimpleBrain(this)

    /** 骑乘者 */
    @Expose
    var passengers = CopyOnWriteArraySet<String>()

    /** 控制器 */
    @Expose
    var controller = CopyOnWriteArrayList<Controller>()

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

    /** 管理器 */
    override var manager: Manager? = null
        set(value) {
            if (field != null && value != null) {
                errorBy("error-manager-has-been-initialized")
            }
            field = value
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
            // FIXME
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
                manager!!.delete(this)
                manager = null
                AdyeshachEntityRemoveEvent(this).call()
            }
        }
    }

    @Deprecated("请使用 despawn(destroyPacket, removeFromManager), 该方法存在命名误导", ReplaceWith("despawn()"))
    override fun destroy() {
        despawn()
    }

    @Deprecated("请使用 despawn(destroyPacket, removeFromManager), 该方法存在命名误导", ReplaceWith("despawn(destroyPacket = false, removeFromManager = true)"))
    override fun remove() {
        despawn(destroyPacket = false, removeFromManager = true)
    }

    @Deprecated("请使用 despawn(destroyPacket, removeFromManager), 该方法存在命名误导", ReplaceWith("despawn(removeFromManager = true)"))
    override fun delete() {
        despawn(removeFromManager = true)
    }

    override fun teleport(location: Location) {
        val event = AdyeshachEntityTeleportEvent(this, location)
        if (event.call()) {
            val newPosition = EntityPosition.fromLocation(event.location)
            if (newPosition == position) {
                return
            }
            position = newPosition
            // 切换世界
            if (position.world.name != location.world?.name) {
                despawn()
                respawn()
            } else {
                forViewers { Adyeshach.api().getMinecraftAPI().getEntityOperator().teleportEntity(it, index, location) }
            }
        }
    }

    override fun teleport(entityPosition: EntityPosition) {
        teleport(entityPosition.toLocation())
    }

    override fun teleport(x: Double, y: Double, z: Double) {
        if (x.isFinite() && y.isFinite() && z.isFinite()) {
            teleport(position.toLocation().run {
                this.x = x
                this.y = y
                this.z = z
                this
            })
        }
    }

    override fun setVelocity(vector: org.bukkit.util.Vector) {
        forViewers {
            Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityVelocity(it, index, vector)
        }
    }

    override fun setHeadRotation(yaw: Float, pitch: Float, forceUpdate: Boolean) {
        val event = AdyeshachHeadRotationEvent(this, yaw, pitch)
        if (event.call()) {
            // 如果数字变更，则更新视角
            val hasUpdate = position.yaw != yaw || position.pitch != pitch
            if (hasUpdate || forceUpdate) {
                position = position.run {
                    this.yaw = event.yaw
                    this.pitch = event.pitch
                    this
                }
                forViewers { Adyeshach.api().getMinecraftAPI().getEntityOperator().updateHeadRotation(it, index, event.yaw, event.pitch) }
            }
        }
    }

    override fun setAnimation(animation: BukkitAnimation) {
        if (this is ModelEngine && animation == BukkitAnimation.TAKE_DAMAGE) {
            hurt()
        } else {
            forViewers { Adyeshach.api().getMinecraftAPI().getEntityOperator().updateEntityAnimation(it, index, animation) }
        }
    }

    override fun onTick() {
        // 所在区块是否加载
        if (getChunkAccess(getWorld()).isChunkLoaded(floor(x).toInt() shr 4, floor(z).roundToInt() shr 4)) {
            // 确保客户端显示实体正常
            if (viewPlayers.visibleRefreshLocker.hasNext()) {
                // 复活在可视范围内的实体
                viewPlayers.getOutsidePlayers { isInVisibleDistance(it) }.forEach { player ->
                    if (visible(player, true)) {
                        viewPlayers.visible += player.name
                    }
                }
                // 销毁不在可视范围内的实体
                viewPlayers.getViewPlayers { !isInVisibleDistance(it) }.forEach { player ->
                    if (visible(player, false) && !CompatServerTours.isRoutePlaying(player)) {
                        viewPlayers.visible -= player.name
                    }
                }
            }
            // 只有存在可见玩家时才会处理实体控制器
            if (viewPlayers.hasVisiblePlayer()) {
                // 实体逻辑处理
                brain.tick()
            }
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

    companion object {

        private val chunkAccess = ConcurrentHashMap<String, ChunkAccess>()

        fun getChunkAccess(world: World): ChunkAccess {
            return if (chunkAccess.contains(world.name)) {
                chunkAccess[world.name]!!
            } else {
                val access = ChunkAccess(world)
                chunkAccess[world.name] = access
                access
            }
        }

        @SubscribeEvent
        private fun onUnload(e: WorldUnloadEvent) {
            submitAsync { chunkAccess.remove(e.world.name) }
        }
    }
}