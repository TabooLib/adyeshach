package ink.ptms.adyeshach.core.entity.controller

import ink.ptms.adyeshach.core.entity.EntityInstance
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ai.ControllerGenerator
 *
 * @author 坏黑
 * @since 2022/6/15 23:38
 */
open class ControllerGenerator(val type: Class<out Controller>, protected val func: Function<EntityInstance, Controller>) {

    fun generate(entity: EntityInstance): Controller {
        return func.apply(entity)
    }
}