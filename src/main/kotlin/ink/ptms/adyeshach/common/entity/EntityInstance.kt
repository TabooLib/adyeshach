package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.entity.element.EntityPosition
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.util.Indexs
import ink.ptms.adyeshach.nms.NMS
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-04 12:51
 */
abstract class EntityInstance(val owner: Player, entityTypes: EntityTypes) : EntityBase(entityTypes) {

    val index = Indexs.nextIndex(owner)

    open fun spawn(location: Location) {
        this.world = location.world!!.name
        this.position = EntityPosition.fromLocation(location)
        setHeadRotation(location.yaw, location.pitch)
    }

    open fun destroy() {
        NMS.INSTANCE.destroyEntity(owner, index)
    }

    open fun teleport(world: World, entityPosition: EntityPosition) {
        NMS.INSTANCE.teleportEntity(owner, index, entityPosition.toLocation(world))
    }

    open fun teleport(location: Location) {
        NMS.INSTANCE.teleportEntity(owner, index, location)
    }

    open fun setHeadRotation(yaw: Float, pitch: Float) {
        position = position.run {
            this.yaw = yaw
            this.pitch = pitch
            this
        }
        NMS.INSTANCE.setHeadRotation(owner, index, yaw, pitch)
    }

    open fun controllerLook(targetLocation: Location) {
        val entityLocation = position.toLocation(targetLocation.world!!).also {
            it.y += (entityType.entitySize.height * 0.85)
        }
        entityLocation.direction = targetLocation.subtract(entityLocation).toVector()
        setHeadRotation(entityLocation.yaw, entityLocation.pitch)
    }

    open fun isFired(): Boolean {
        return properties.onFire
    }

    open fun isSneaking(): Boolean {
        return properties.crouched
    }

    open fun isSprinting(): Boolean {
        return properties.sprinting
    }

    open fun isSwimming(): Boolean {
        return properties.swimming
    }

    open fun isInvisible(): Boolean {
        return properties.invisible
    }

    open fun isGlowing(): Boolean {
        return properties.glowing
    }

    open fun isFlyingElytra(): Boolean {
        return properties.flyingElytra
    }

    open fun setFired(onFire: Boolean) {
        properties.onFire = onFire
        updateMetadata()
    }

    open fun setSneaking(sneaking: Boolean) {
        properties.crouched = sneaking
        updateMetadata()
    }

    open fun setSprinting(sprinting: Boolean) {
        properties.sprinting = sprinting
        updateMetadata()
    }

    open fun setSwimming(swimming: Boolean) {
        properties.swimming = swimming
        updateMetadata()
    }

    open fun setInvisible(invisible: Boolean) {
        properties.invisible = invisible
        updateMetadata()
    }

    open fun setGlowing(glowing: Boolean) {
        properties.glowing = glowing
        updateMetadata()
    }

    open fun setFlyingElytra(flyingElytra: Boolean) {
        properties.flyingElytra = flyingElytra
        updateMetadata()
    }

    protected open fun updateMetadata() {
        NMS.INSTANCE.updateEntityMetadata(owner, index, NMS.INSTANCE.getMetaEntityProperties(
                onFire = properties.onFire,
                crouched = properties.crouched,
                unUsedRiding = properties.unUsedRiding,
                sprinting = properties.sprinting,
                swimming = properties.swimming,
                invisible = properties.invisible,
                glowing = properties.glowing,
                flyingElytra = properties.flyingElytra
        ))
    }
}