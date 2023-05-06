package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
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
                    errorBy("error-no-manager-or-entity-selected")
                }
                val entities = script.getEntities()
                if (entities.size > 1) entities.map { e -> e.getLocation() } else entities.first().getLocation()
            }
        }
    }
}