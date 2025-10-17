package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.core.util.plus
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * npc position
 */
@KetherParser(["npc"], namespace = "adyeshach", shared = true)
private fun actionNPC() = scriptParser {
    it.switch {
        case("position", "location") {
            actionNow {
                val script = script()
                if (script.getManager() == null || !script.isEntitySelected()) {
                    script.throwUndefinedError()
                }
                val entities = script.getEntities()
                if (entities.size > 1) entities.map { e -> e.getLocation() } else entities.first().getLocation()
            }
        }
        case("eye-position", "eye-location") {
            val fixed = try {
                it.mark()
                it.expects("fixed")
                it.nextDouble()
            } catch (_: Exception) {
                it.reset()
                0.0
            }
            actionNow {
                val script = script()
                if (script.getManager() == null || !script.isEntitySelected()) {
                    script.throwUndefinedError()
                }
                val entities = script.getEntities()
                if (entities.size > 1) entities.map { e -> e.getEyeLocation().plus(y = fixed) } else entities.first().getEyeLocation().plus(y = fixed)
            }
        }
    }
}