package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller

/**
 * @author sky
 * @since 2020-08-19 22:09
 */
@Deprecated("Outdated but usable")
class GeneralGravity(entity: EntityInstance) : Controller(entity) {

    var isGravity = true
    var isOnGround = true
        private set

    override fun shouldExecute(): Boolean {
        return false
    }

    override fun onTick() {
    }
}