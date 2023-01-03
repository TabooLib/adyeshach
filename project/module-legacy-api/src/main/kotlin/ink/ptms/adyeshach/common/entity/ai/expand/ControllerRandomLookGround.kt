package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller

/**
 * 随机看向附近
 *
 * @author sky
 * @since 2020-08-19 22:09
 */
@Deprecated("Outdated but usable")
class ControllerRandomLookGround(entity: EntityInstance) : Controller(entity) {

    override fun isAsync(): Boolean {
        return true
    }

    override fun shouldExecute(): Boolean {
        return false
    }

    override fun onTick() {
    }
}