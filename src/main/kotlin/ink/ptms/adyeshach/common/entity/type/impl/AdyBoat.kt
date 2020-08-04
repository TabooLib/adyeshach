package ink.ptms.adyeshach.common.entity.type.impl

import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitBoat
import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/4 22:48
 */
class AdyBoat(owner: Player) : AdyEntity(owner, EntityTypes.BOAT), MetadataExtend {

    init {
        properties = BoatProperties()
    }

    fun setTimeSinceLastHit(time: Int) {
        getProperties().sinceLastHit = time
        update()
    }

    fun getTimeSinceLastHit(): Int {
        return getProperties().sinceLastHit
    }

    fun setForwardDirection(direction: Int) {
        getProperties().forwardDirection = direction
        update()
    }

    fun getForwardDirection(): Int {
        return getProperties().forwardDirection
    }

    fun setDamageTaken(damageTaken: Float) {
        getProperties().damageTaken = damageTaken
        update()
    }

    fun getDamageTaken(): Float {
        return getProperties().damageTaken
    }

    fun setType(type: BukkitBoat) {
        getProperties().type = type
        update()
    }

    fun getType(): BukkitBoat {
        return getProperties().type
    }

    fun setLeftPaddleTurning(boolean: Boolean) {
        getProperties().leftPaddleTurning = boolean
        update()
    }

    fun isLeftPaddleTurning(): Boolean {
        return getProperties().leftPaddleTurning
    }

    fun setRightPaddleTurning(boolean: Boolean) {
        getProperties().rightPaddleTurning = boolean
        update()
    }

    fun isRightPaddleTurning(): Boolean {
        return getProperties().rightPaddleTurning
    }

    fun setSplashTimer(timer: Int) {
        getProperties().splashTimer = timer
        update()
    }

    fun getSplashTimer(): Int {
        return getProperties().splashTimer
    }

    private fun update() {
        NMS.INSTANCE.updateEntityMetadata(owner, index, *metadata().toTypedArray())
    }

    override fun metadata(): List<Any> {
        return getProperties().run {
            listOf(
                    NMS.INSTANCE.getMetaEntityInt(7, sinceLastHit),
                    NMS.INSTANCE.getMetaEntityInt(8, forwardDirection),
                    NMS.INSTANCE.getMetaEntityFloat(9, damageTaken),
                    NMS.INSTANCE.getMetaEntityInt(10, type.ordinal),
                    NMS.INSTANCE.getMetaEntityBoolean(11, leftPaddleTurning),
                    NMS.INSTANCE.getMetaEntityBoolean(12, rightPaddleTurning),
                    NMS.INSTANCE.getMetaEntityInt(13, splashTimer),
            )
        }
    }

    private fun getProperties(): BoatProperties = properties as BoatProperties

    private class BoatProperties : EntityProperties() {

        @Expose
        var sinceLastHit = 0

        @Expose
        var forwardDirection = 1

        @Expose
        var damageTaken = 0.0F

        @Expose
        var type = BukkitBoat.OAK

        @Expose
        var leftPaddleTurning = false

        @Expose
        var rightPaddleTurning = false

        @Expose
        var splashTimer = 0
    }
}