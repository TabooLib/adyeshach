package ink.ptms.adyeshach.common.entity.ai

import ink.ptms.adyeshach.common.entity.EntityInstance
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.ai.ControllerGenerator
 *
 * @author 坏黑
 * @since 2022/6/15 23:38
 */
class ControllerGenerator(val type: Class<out Controller>, val func: Function<EntityInstance, Controller>) {

    operator fun invoke(entity: EntityInstance): Controller {
        return func.apply(entity)
    }
}