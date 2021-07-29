package ink.ptms.adyeshach.common.entity.ai

import ink.ptms.adyeshach.common.entity.EntityInstance

class ControllerNone(entity: EntityInstance, val name: String) : Controller(entity) {

    override fun shouldExecute(): Boolean {
        return false
    }

    override fun onTick() {
    }
}