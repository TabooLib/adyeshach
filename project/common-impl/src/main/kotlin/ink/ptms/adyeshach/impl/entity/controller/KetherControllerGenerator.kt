package ink.ptms.adyeshach.impl.entity.controller

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.controller.Controller
import ink.ptms.adyeshach.core.entity.controller.ControllerGenerator
import taboolib.library.configuration.ConfigurationSection

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ai.ControllerGenerator
 *
 * @author 坏黑
 * @since 2022/6/15 23:38
 */
class KetherControllerGenerator(val section: ConfigurationSection) : ControllerGenerator(KetherController::class.java, { e -> KetherController(e, section) }) {

    val name = section.name

    operator fun invoke(entity: EntityInstance): Controller {
        return func.apply(entity)
    }
}