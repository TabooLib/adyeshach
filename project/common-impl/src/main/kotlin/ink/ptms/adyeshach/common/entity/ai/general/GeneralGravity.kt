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
class GeneralGravity(entity: EntityInstance) : Controller(entity) {

    var isGravity = true

    var isOnGround = true
        private set

    override fun shouldExecute(): Boolean {
        TODO("Not yet implemented")
    }
}