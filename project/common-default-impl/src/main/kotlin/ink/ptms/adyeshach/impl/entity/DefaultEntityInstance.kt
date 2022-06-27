package ink.ptms.adyeshach.impl.entity

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import ink.ptms.adyeshach.common.entity.*
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.util.Indexs
import io.netty.util.internal.ConcurrentSet
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
 *
 * @author 坏黑
 * @since 2022/6/19 21:26
 */
abstract class DefaultEntityInstance(entityType: EntityTypes) :
    DefaultEntityBase(entityType), EntityInstance, DefaultControllable, DefaultGenericEntity, DefaultRideable, DefaultViewable, InternalEntity {

    override val index: Int = Indexs.nextIndex()

    override var manager: Manager? = null
        set(value) {
            if (field != null) {
                error("Entity Manager has been initialized.")
            }
            field = value
        }

    override var isDeleted = false

    @Expose
    override var moveSpeed = 0.2

    @Expose
    val controller = CopyOnWriteArraySet<Controller>()

    @Expose
    var passengers = CopyOnWriteArraySet<String>()

    @Expose
    override var visibleDistance = -1.0
        get() = (if (field == -1.0) TODO("Not yet implemented") else field)

    @Expose
    override var visibleAfterLoaded = true
        set(value) {
            if (!isPublic()) {
                error("Cannot set visibleAfterLoaded for private entity")
            }
            if (!value) {
                clearViewer()
            }
            field = value
        }

    @Suppress("LeakingThis")
    override val viewPlayers = DefaultViewPlayers(this)

    @Expose
    var modelEngineName = ""
        set(value) {
            field = value
            if (this is ModelEngine) {
                refreshModelEngine()
            }
        }

    var modelEngineUniqueId: UUID? = null

    override fun spawn(viewer: Player, spawn: Runnable): Boolean {
        TODO("Not yet implemented")
    }

    override fun destroy(viewer: Player, destroy: Runnable): Boolean {
        TODO("Not yet implemented")
    }

    override fun isPublic(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isTemporary(): Boolean {
        TODO("Not yet implemented")
    }

    override fun spawn(location: Location) {
        TODO("Not yet implemented")
    }

    override fun respawn() {
        TODO("Not yet implemented")
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }

    override fun remove() {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override fun teleport(location: Location) {
        TODO("Not yet implemented")
    }

    override fun teleport(entityPosition: EntityPosition) {
        TODO("Not yet implemented")
    }

    override fun teleport(x: Double, y: Double, z: Double) {
        TODO("Not yet implemented")
    }

    override fun setVelocity(vector: Vector) {
        TODO("Not yet implemented")
    }

    override fun setHeadRotation(yaw: Float, pitch: Float) {
        TODO("Not yet implemented")
    }

    override fun setAnimation(animation: BukkitAnimation) {
        TODO("Not yet implemented")
    }
}