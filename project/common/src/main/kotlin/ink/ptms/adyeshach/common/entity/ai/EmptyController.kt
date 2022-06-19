package ink.ptms.adyeshach.common.entity.ai

import ink.ptms.adyeshach.common.entity.EntityInstance

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.ai.EmptyController
 *
 * @author 坏黑
 * @since 2022/6/15 23:38
 */
class EmptyController(entity: EntityInstance, val name: String) : Controller(entity) {

    override fun shouldExecute(): Boolean {
        return false
    }

    override fun onTick() {
    }
}