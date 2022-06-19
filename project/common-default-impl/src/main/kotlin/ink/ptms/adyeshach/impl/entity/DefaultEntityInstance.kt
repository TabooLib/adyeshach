package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import ink.ptms.adyeshach.common.entity.EntityBase
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.InternalEntity
import ink.ptms.adyeshach.common.entity.manager.Manager
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.util.Vector

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
 *
 * @author 坏黑
 * @since 2022/6/19 21:26
 */
abstract class DefaultEntityInstance(entityType: EntityTypes) : DefaultEntityBase(entityType), EntityInstance, DefaultControllable, DefaultGenericEntity,
    DefaultViewable, InternalEntity {

    override val index: Int
        get() = TODO("Not yet implemented")

    override var manager: Manager?
        get() = TODO("Not yet implemented")
        set(value) {}

    override var isDeleted: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

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