package ink.ptms.adyeshach.core.entity.controller

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ai.PrepareController
 *
 * @author 坏黑
 * @since 2022/6/19 23:01
 */
class PrepareController(val generator: ControllerGenerator): Controller() {

    override fun id(): String {
        return "prepare_${generator.type.name}"
    }

    override fun shouldExecute(): Boolean {
        return true
    }
}