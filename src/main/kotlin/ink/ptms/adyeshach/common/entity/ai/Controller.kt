package ink.ptms.adyeshach.common.entity.ai

import ink.ptms.adyeshach.common.entity.EntityInstance

/**
 * @author sky
 * @since 2020-08-19 21:07
 */
abstract class Controller(val entity: EntityInstance? = null) {

    abstract fun shouldExecute(): Boolean

    abstract fun onTick()

    open fun isAsync(): Boolean {
        return false
    }

    class Pre(val controller: ControllerGenerator): Controller() {

        override fun shouldExecute(): Boolean {
            return true
        }

        override fun onTick() {
        }
    }
}