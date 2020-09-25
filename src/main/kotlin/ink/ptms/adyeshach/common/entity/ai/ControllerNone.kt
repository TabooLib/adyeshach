package ink.ptms.adyeshach.common.entity.ai

import ink.ptms.adyeshach.common.entity.EntityInstance
import io.izzel.taboolib.module.dependency.TDependency
import io.izzel.taboolib.util.lite.Numbers

class ControllerNone(entity: EntityInstance, val name: String) : Controller(entity) {

    override fun shouldExecute(): Boolean {
        return Numbers.random(0.2)
    }

    override fun onTick() {
        println("[Adyeshach] Unknown serializable controller: $name")
    }
}