package ink.ptms.adyeshach.core.entity.controller

import com.google.gson.JsonElement

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ai.PrepareController
 *
 * @author 坏黑
 * @since 2022/6/19 23:01
 */
open class PrepareController(val generator: ControllerGenerator, override val source: JsonElement): Controller(), ControllerSource {

    override fun id(): String {
        return "prepare:${generator.type.simpleName}"
    }

    override fun shouldExecute(): Boolean {
        return true
    }

    override fun toString(): String {
        return id()
    }
}