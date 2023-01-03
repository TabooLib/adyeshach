package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller

/**
 * @author sky
 * @since 2020-08-19 22:09
 */
@Deprecated("Outdated and unavailable")
class GeneralSmoothLook(entity: EntityInstance) : Controller(entity) {

    var yaw = 0f
    var pitch = 0f
    var interval = 22.5f
    var isReset = true
    var isLooking = false

    override fun shouldExecute(): Boolean {
        return false
    }

    override fun onTick() {
    }
}