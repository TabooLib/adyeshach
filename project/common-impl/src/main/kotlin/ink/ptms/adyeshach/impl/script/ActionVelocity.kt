package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import taboolib.module.kether.script

/**
 * velocity 0 1 0
 */
@KetherParser(["velocity"], namespace = "adyeshach", shared = true)
private fun parser() = combinationParser {
    it.group(double(), double(), double()).apply(it) { x, y, z ->
        now {
            if (script().getManager() == null || !script().isEntitySelected()) {
                script().throwUndefinedError()
            }
            script().getEntities().forEach { e -> e.setVelocity(x, y, z) }
        }
    }
}