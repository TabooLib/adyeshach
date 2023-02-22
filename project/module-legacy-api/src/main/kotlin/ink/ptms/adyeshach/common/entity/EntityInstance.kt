@file:Suppress("DuplicatedCode")

package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.event.*
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.EntityPosition
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerLookAtPlayer
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerLookAtPlayerAlways
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerRandomLookGround
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.entity.manager.LegacyManager
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.util.serializer.UnknownWorldException
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.util.Components
import ink.ptms.adyeshach.core.util.getEnumOrNull
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.warning
import taboolib.common.util.Vector
import taboolib.common.util.unsafeLazy
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.function.Consumer

/**
 * @author sky
 * @since 2020-08-04 12:51
 */
@Deprecated("Outdated but usable")
abstract class EntityInstance(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : EntityBase(entityTypes, v2) {

    val index by unsafeLazy { v2.index }

    val viewPlayers = ViewPlayers(this)

    var visibleDistance: Double
        get() = v2.visibleDistance
        set(value) {
            v2.visibleDistance = value
        }

    var visibleAfterLoaded: Boolean
        get() = v2.visibleAfterLoaded
        set(value) {
            v2.visibleAfterLoaded = value
        }

    var moveSpeed: Double
        get() = v2.moveSpeed
        set(value) {
            v2.moveSpeed = value
        }

    var modelEngineName: String
        get() = (v2 as ModelEngine).modelEngineName
        set(value) {
            (v2 as ModelEngine).modelEngineName = value
        }

    var modelEngineUniqueId: UUID?
        get() = (v2 as ModelEngine).modelEngineUniqueId
        set(value) {
            (v2 as ModelEngine).modelEngineUniqueId = value
        }

    var manager: Manager?
        get() = if (v2.manager != null) LegacyManager(v2.manager!!) else null
        set(value) {
            v2.manager = value?.v2()
        }

    var isFreeze: Boolean
        get() = v2.isFreeze
        set(value) {
            v2.isFreeze = value
        }

    var isNoAi: Boolean
        get() = v2.isNitwit
        set(value) {
            v2.isNitwit = value
        }

    var isDeleted: Boolean
        get() = v2.isRemoved
        set(value) {
        }

    fun getDisplayName(): String {
        return v2.getDisplayName()
    }

    abstract fun visible(viewer: Player, visible: Boolean): Boolean

    fun isPublic(): Boolean {
        return manager?.isPublic() == true
    }

    fun isTemporary(): Boolean {
        return manager?.v2()?.isTemporary() == true
    }

    fun forViewers(viewer: Consumer<Player>) {
        viewPlayers.getViewPlayers().forEach { viewer.accept(it) }
    }

    fun addViewer(viewer: Player) {
        v2.addViewer(viewer)
    }

    fun removeViewer(viewer: Player) {
        v2.removeViewer(viewer)
    }

    fun clearViewer() {
        v2.clearViewer()
    }

    fun isViewer(viewer: Player): Boolean {
        return v2.isViewer(viewer)
    }

    fun hasViewer(): Boolean {
        return v2.hasViewer()
    }

    fun isVisibleViewer(viewer: Player): Boolean {
        return v2.isVisibleViewer(viewer)
    }

    fun spawn(location: Location) {
        v2.spawn(location)
    }

    fun respawn() {
        v2.respawn()
    }

    fun destroy() {
        v2.despawn()
    }

    fun remove() {
        v2.despawn(destroyPacket = false, removeFromManager = true)
    }

    fun delete() {
        v2.remove()
    }

    @Deprecated("Use teleportFuture instead", ReplaceWith("teleportFuture(location)"))
    fun teleport(location: Location) {
        teleportFuture(location)
    }

    fun teleportFuture(location: Location): CompletableFuture<*> {
        v2.teleport(location)
        return CompletableFuture.completedFuture(null)
    }

    @Deprecated("Use teleportFuture instead", ReplaceWith("teleportFuture(entityPosition)"))
    fun teleport(entityPosition: EntityPosition) {
        teleport(entityPosition.toLocation())
    }

    fun teleportFuture(entityPosition: EntityPosition): CompletableFuture<*> {
        return teleportFuture(entityPosition.toLocation())
    }

    @Deprecated("Use teleportFuture instead", ReplaceWith("teleportFuture(x, y, z)"))
    fun teleport(x: Double, y: Double, z: Double) {
        teleportFuture(x, y, z)
    }

    fun teleportFuture(x: Double, y: Double, z: Double): CompletableFuture<*> {
        v2.teleport(x, y, z)
        return CompletableFuture.completedFuture(null)
    }

    fun setHeadRotation(yaw: Float, pitch: Float) {
        setHeadRotation(yaw, pitch, false)
    }

    fun setHeadRotation(yaw: Float, pitch: Float, force: Boolean = false) {
        v2.setHeadRotation(yaw, pitch, force)
    }

    fun getController(): List<Controller> {
        IllegalStateException("Outdated api is being called, please contact the plugin author to update.").printStackTrace()
        return emptyList()
    }

    fun <T : Controller> getController(controller: Class<T>): T? {
        IllegalStateException("Outdated api is being called, please contact the plugin author to update.").printStackTrace()
        return null
    }

    fun registerController(controller: Controller) {
        val cv2 = when (controller) {
            is ControllerLookAtPlayer -> ink.ptms.adyeshach.impl.entity.controller.ControllerLookAtPlayer(v2)
            is ControllerLookAtPlayerAlways -> ink.ptms.adyeshach.impl.entity.controller.ControllerLookAtPlayer(v2, 8.0, 1.0)
            is ControllerRandomLookGround -> ink.ptms.adyeshach.impl.entity.controller.ControllerRandomLookaround(v2)
            is GeneralGravity, is GeneralMove, is GeneralSmoothLook -> return
            else -> null
        }
        if (cv2 != null) {
            v2.registerController(cv2)
        } else {
            warning("Controller ${controller.javaClass.simpleName} is not supported in legacy api.")
        }
    }

    fun <T : Controller> unregisterController(controller: Class<T>) {
        when (controller) {
            ControllerLookAtPlayer::class.java -> v2.unregisterController(ink.ptms.adyeshach.impl.entity.controller.ControllerLookAtPlayer::class.java)
            ControllerLookAtPlayerAlways::class.java -> v2.unregisterController(ink.ptms.adyeshach.impl.entity.controller.ControllerLookAtPlayer::class.java)
            ControllerRandomLookGround::class.java -> v2.unregisterController(ink.ptms.adyeshach.impl.entity.controller.ControllerRandomLookaround::class.java)
            GeneralGravity::class.java, GeneralMove::class.java, GeneralSmoothLook::class.java -> return
        }
    }

    fun resetController() {
        v2.resetController()
    }

    fun isTryMoving(): Boolean {
        return hasTag(StandardTags.IS_PATHFINDING)
    }

    fun isControllerMoving(): Boolean {
        return hasTag(StandardTags.IS_MOVING)
    }

    fun isControllerJumping(): Boolean {
        return false
    }

    fun isControllerOnGround(): Boolean {
        IllegalStateException("Outdated api is being called, please contact the plugin author to update.").printStackTrace()
        return false
    }

    fun controllerLook(location: Location, smooth: Boolean = false, smoothInternal: Float = 22.5f) {
        v2.controllerLookAt(location.x, location.y, location.z)
    }

    fun controllerLook(yaw: Float, pitch: Float, smooth: Boolean = false, smoothInternal: Float = 22.5f) {
        setHeadRotation(yaw, pitch)
    }

    fun controllerMove(location: Location, pathType: PathType = entityType.getPathType(), speed: Double = moveSpeed) {
        v2.moveTarget = location
    }

    fun controllerMoveWithPathList(
        location: Location,
        pathList: List<org.bukkit.util.Vector>,
        pathType: PathType = entityType.getPathType(),
        speed: Double = moveSpeed,
    ) {
        v2.moveTarget = location
    }

    fun controllerStill() {
        v2.moveFrames = null
    }

    fun displayAnimation(animation: BukkitAnimation) {
        v2.sendAnimation(animation.v2())
    }

    fun isVehicle(): Boolean {
        return getPassengers().isNotEmpty()
    }

    fun hasVehicle(): Boolean {
        return getVehicle() != null
    }

    fun getVehicle(): EntityInstance? {
        val vehicle = v2.getVehicle() ?: return null
        return manager?.getEntityByUniqueId(vehicle.uniqueId)
    }

    fun getPassengers(): List<EntityInstance> {
        if (manager == null) return emptyList()
        return v2.getPassengers().mapNotNull { manager!!.getEntityByUniqueId(it.uniqueId) }
    }

    fun addPassenger(vararg entity: EntityInstance) {
        v2.addPassenger(*entity.map { it.v2 }.toTypedArray())
    }

    fun removePassenger(vararg entity: EntityInstance) {
        v2.removePassenger(*entity.map { it.v2 }.toTypedArray())
    }

    fun removePassenger(vararg id: String) {
        v2.removePassenger(*id)
    }

    fun clearPassengers() {
        v2.clearPassengers()
    }

    fun refreshPassenger(viewer: Player, error: Boolean = true) {
        v2.refreshPassenger(viewer)
    }

    fun refreshPassenger() {
        v2.refreshPassenger()
    }

    fun isInVisibleDistance(player: Player): Boolean {
        return v2.isInVisibleDistance(player)
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
        return Components.toLegacyText(getMetadata("customName"))
    }

    fun setPose(pose: BukkitPose) {
        val v2 = ink.ptms.adyeshach.core.bukkit.BukkitPose::class.java.getEnumOrNull(pose.name) ?: ink.ptms.adyeshach.core.bukkit.BukkitPose.STANDING
        setMetadata("pose", v2)
    }

    fun getPose(): BukkitPose {
        val v2 = getMetadata<ink.ptms.adyeshach.core.bukkit.BukkitPose>("pose")
        return BukkitPose::class.java.getEnumOrNull(v2.name) ?: BukkitPose.STANDING
    }

    var ticksFrozenInPowderedSnow: Int
        get() = getMetadata("ticksFrozenInPowderedSnow")
        set(value) {
            setMetadata("ticksFrozenInPowderedSnow", value)
        }

    override fun setMetadata(key: String, value: Any): Boolean {
        return v2.setMetadata(key, value)
    }

    open fun updateEntityMetadata() {
        v2.updateEntityMetadata()
    }

    open fun updateEntityMetadata(viewer: Player) {
        v2.updateEntityMetadata(viewer)
    }

    open fun generateEntityMetadata(player: Player): Array<Any> {
        return v2.generateEntityMetadata(player).map { it.source() }.toTypedArray()
    }

    open fun sendVelocity(vector: Vector) {
        v2.sendVelocity(org.bukkit.util.Vector(vector.x, vector.y, vector.z))
    }

    open fun openEditor(player: Player) {
        Adyeshach.editor()?.openEditor(player, v2)
    }

    open fun onTick() {
    }

    @Throws(UnknownWorldException::class)
    open fun clone(newId: String, location: Location, manager: Manager? = null): EntityInstance? {
        val clone = v2.clone(newId, location, manager?.v2()) ?: return null
        return manager?.getEntityByUniqueId(clone.uniqueId)
    }

    open fun showModelEngine(viewer: Player): Boolean {
        v2 as ModelEngine
        return v2.showModelEngine(viewer)
    }

    open fun hideModelEngine(viewer: Player): Boolean {
        v2 as ModelEngine
        return v2.hideModelEngine(viewer)
    }

    open fun refreshModelEngine(): Boolean {
        v2 as ModelEngine
        return v2.refreshModelEngine()
    }

    open fun updateModelEngineNameTag() {
        v2 as ModelEngine
        v2.updateModelEngineNameTag()
    }

    companion object {

        val pool = Executors.newFixedThreadPool(16)!!
    }
}