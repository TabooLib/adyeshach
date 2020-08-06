package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.util.Indexs
import io.izzel.taboolib.util.chat.TextComponent
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 12:51
 */
abstract class EntityInstance(val owner: Player, entityTypes: EntityTypes) : EntityBase(entityTypes) {

    val index = Indexs.nextIndex(owner)

    init {
        /**
         * 1.13 -> 无 Index=6
         * 1.12 -> 无 0x04
         * 1.9 -> 同 1.12, 删去 Index=5
         */
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
        registerMeta(4, "isSilent", false)
        registerMeta(5, "noGravity", false)
//        registerMeta(6, "pose", 0)
    }

    open fun spawn(location: Location) {
        this.world = location.world!!.name
        this.position = EntityPosition.fromLocation(location)
        setHeadRotation(location.yaw, location.pitch)
    }

    open fun destroy() {
        NMS.INSTANCE.destroyEntity(owner, index)
    }

    open fun teleport(location: Location) {
        this.world = location.world!!.name
        this.position = EntityPosition.fromLocation(location)
        NMS.INSTANCE.teleportEntity(owner, index, location)
    }

    open fun controllerLook(location: Location) {
        val entityLocation = position.toLocation(location.world!!).also {
            it.y += (entityType.entitySize.width * 0.85)
        }
        entityLocation.direction = location.clone().subtract(entityLocation).toVector()
        setHeadRotation(entityLocation.yaw, entityLocation.pitch)
    }

    open fun setHeadRotation(yaw: Float, pitch: Float) {
        position = position.run {
            this.yaw = yaw
            this.pitch = pitch
            this
        }
        NMS.INSTANCE.setHeadRotation(owner, index, yaw, pitch)
    }

    open fun isFired(): Boolean {
        return getMetadata("onFire")
    }

    open fun isSneaking(): Boolean {
        return getMetadata("isCrouched")
    }

    open fun isSprinting(): Boolean {
        return getMetadata("isSprinting")
    }

    open fun isSwimming(): Boolean {
        return getMetadata("isSwimming")
    }

    open fun isInvisible(): Boolean {
        return getMetadata("isInvisible")
    }

    open fun isGlowing(): Boolean {
        return getMetadata("isGlowing")
    }

    open fun isFlyingElytra(): Boolean {
        return getMetadata("isFlyingElytra")
    }

    open fun isNoGravity(): Boolean {
        return getMetadata("noGravity")
    }

    open fun setFired(onFire: Boolean) {
        setMetadata("onFire", onFire)
    }

    open fun setSneaking(sneaking: Boolean) {
        setMetadata("sneaking", sneaking)
    }

    open fun setSprinting(sprinting: Boolean) {
        setMetadata("isSprinting", sprinting)
    }

    open fun setSwimming(swimming: Boolean) {
        setMetadata("isSwimming", swimming)
    }

    open fun setInvisible(invisible: Boolean) {
        setMetadata("isInvisible", invisible)
    }

    open fun setGlowing(glowing: Boolean) {
        setMetadata("isGlowing", glowing)
    }

    open fun setFlyingElytra(flyingElytra: Boolean) {
        setMetadata("isFlyingElytra", flyingElytra)
    }

    open fun setNoGravity(noGravity: Boolean) {
        setMetadata("noGravity", noGravity)
    }
}