package ink.ptms.adyeshach.core.entity.controller

import ink.ptms.adyeshach.core.entity.EntityInstance

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ai.EmptyController
 *
 * @author 坏黑
 * @since 2022/6/15 23:38
 */
class EmptyController(entity: EntityInstance, val name: String) : Controller(entity) {

    override fun id(): String {
        return "empty"
    }

    override fun shouldExecute(): Boolean {
        return false
    }
}