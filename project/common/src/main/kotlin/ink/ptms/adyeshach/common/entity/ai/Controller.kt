package ink.ptms.adyeshach.common.entity.ai

import ink.ptms.adyeshach.common.entity.EntityInstance

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.ai.Controller
 *
 * @author 坏黑
 * @since 2022/6/15 23:38
 */
abstract class Controller(val entity: EntityInstance? = null) {

    abstract fun shouldExecute(): Boolean

    abstract fun onTick()

    open fun isAsync(): Boolean {
        return false
    }
}