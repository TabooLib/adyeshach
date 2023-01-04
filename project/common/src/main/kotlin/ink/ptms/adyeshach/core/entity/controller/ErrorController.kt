package ink.ptms.adyeshach.core.entity.controller

import com.google.gson.JsonElement
import ink.ptms.adyeshach.core.entity.EntityInstance

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ai.ErrorController
 *
 * @author 坏黑
 * @since 2022/6/15 23:38
 */
class ErrorController(entity: EntityInstance, override val source: JsonElement, val exception: Throwable) : Controller(entity), ControllerSource {

    override fun id(): String {
        return "error:$source (${source.javaClass.simpleName})"
    }

    override fun shouldExecute(): Boolean {
        return false
    }

    override fun toString(): String {
        return id()
    }
}