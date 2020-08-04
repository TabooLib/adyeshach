package ink.ptms.adyeshach.common.entity.element

import io.izzel.taboolib.internal.gson.annotations.Expose

/**
 * @Author sky
 * @Since 2020-08-04 13:52
 */
open class EntityProperties(
        @Expose
        var onFire: Boolean = false,
        @Expose
        var crouched: Boolean = false,
        @Expose
        var unUsedRiding: Boolean = false,
        @Expose
        var sprinting: Boolean = false,
        @Expose
        var swimming: Boolean = false,
        @Expose
        var invisible: Boolean = false,
        @Expose
        var glowing: Boolean = false,
        @Expose
        var flyingElytra: Boolean = false,
        @Expose
        var gravity: Boolean = true,
) {

    open class Ageable : EntityProperties() {
        @Expose
        var baby = false
    }

    open class Tameable : EntityProperties() {
        @Expose
        var sitting = false
        @Expose
        var angry = false
        @Expose
        var tamed = false
    }

    open class Raider : EntityProperties() {
        @Expose
        var celebrating = false
    }

    open class Zombie : EntityProperties() {
        @Expose
        var baby = false
        @Expose
        var drowning = false
    }

    open class Minecart : EntityProperties() {
        @Expose
        var snakingPower = 0
        @Expose
        var snakingDirection = 1
        @Expose
        var snakingMultiplier = 0f
        @Expose
        var customBlockID = 0
        @Expose
        var customBlockPosition = 6
        @Expose
        var customBlockVisible = false
    }
}