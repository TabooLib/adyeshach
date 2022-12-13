package ink.ptms.adyeshach.common.entity.ai

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.ai.PrepareController
 *
 * @author 坏黑
 * @since 2022/6/19 23:01
 */
class PrepareController(val generator: ControllerGenerator): Controller() {

    override fun shouldExecute(): Boolean {
        return true
    }
}