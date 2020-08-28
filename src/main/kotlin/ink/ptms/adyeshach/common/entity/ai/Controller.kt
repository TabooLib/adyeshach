package ink.ptms.adyeshach.common.entity.ai

import ink.ptms.adyeshach.common.entity.EntityInstance

/**
 * @Author sky
 * @Since 2020-08-19 21:07
 */
abstract class Controller(val entity: EntityInstance) {

    abstract fun shouldExecute(): Boolean

    abstract fun onTick()

}