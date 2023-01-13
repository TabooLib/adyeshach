package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * velocity 0 1 0
 */
@KetherParser(["velocity"], namespace = "adyeshach", shared = true)
private fun parser() = combinationParser {
    it.group(double(), double(), double()).apply(it) { x, y, z ->
        now {
            if (script().getManager() == null || !script().isEntitySelected()) {
                errorBy("error-no-manager-or-entity-selected")
            }
            script().getEntities().forEach { e -> e.setVelocity(x, y, z) }
        }
    }
}