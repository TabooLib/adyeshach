package ink.ptms.adyeshach.common.entity.ai

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.script.KnownController

/**
 * @Author sky
 * @Since 2020-08-19 21:07
 */
abstract class Controller(val entity: EntityInstance? = null) {

    abstract fun shouldExecute(): Boolean

    abstract fun onTick()

    open fun isAsync(): Boolean {
        return false
    }

    class Pre(val controller: KnownController): Controller() {

        override fun shouldExecute(): Boolean {
            return true
        }

        override fun onTick() {
        }
    }
}