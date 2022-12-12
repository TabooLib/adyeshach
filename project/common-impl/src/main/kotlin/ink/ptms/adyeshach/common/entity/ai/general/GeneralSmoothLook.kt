package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
 *
 * @author 坏黑
 * @since 2022/6/28 12:53
 */
class GeneralSmoothLook(entity: EntityInstance) : Controller(entity) {

    var yaw = 0f
        set(value) {
            field = normalizeYaw(value)
        }

    var pitch = 0f
    var interval = 22.5f

    var isReset = true
    var isLooking = false

    override fun shouldExecute(): Boolean {
        TODO("Not yet implemented")
    }

    override fun onTick() {
        TODO("Not yet implemented")
    }

    fun normalizeYaw(yaw: Float): Float {
        var y = yaw
        y %= 360.0f
        if (y >= 180.0f) {
            y -= 360.0f
        } else if (y < -180.0f) {
            y += 360.0f
        }
        return y
    }
}