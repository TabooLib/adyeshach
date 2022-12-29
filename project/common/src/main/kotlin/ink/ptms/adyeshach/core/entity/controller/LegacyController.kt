package ink.ptms.adyeshach.core.entity.controller

import com.google.gson.JsonElement
import ink.ptms.adyeshach.core.entity.EntityInstance

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ai.LegacyController
 *
 * @author 坏黑
 * @since 2022/6/15 23:38
 */
class LegacyController(entity: EntityInstance, override val source: JsonElement) : Controller(entity), ControllerSource {

    override fun id(): String {
        return "legacy:$source"
    }

    override fun shouldExecute(): Boolean {
        return false
    }

    override fun toString(): String {
        return id()
    }
}