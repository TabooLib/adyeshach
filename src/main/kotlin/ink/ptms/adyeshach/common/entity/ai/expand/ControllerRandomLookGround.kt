package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import taboolib.common.util.random

/**
 * 随机看向附近
 *
 * @author sky
 * @since 2020-08-19 22:09
 */
class ControllerRandomLookGround(entity: EntityInstance) : Controller(entity) {

    override fun isAsync(): Boolean {
        return true
    }

    override fun shouldExecute(): Boolean {
        return random(0.01) && (entity!!.getTag("isFreeze") == "true" || !entity.isControllerMoving())
    }

    override fun onTick() {
        entity!!.position.run {
            entity.controllerLook(yaw + random(-90, 90).toFloat(), random(-1, 1).toFloat())
        }
    }
}