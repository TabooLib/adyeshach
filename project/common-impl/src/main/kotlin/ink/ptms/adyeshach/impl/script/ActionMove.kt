package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import org.bukkit.Location
import taboolib.common.platform.function.submitAsync
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

@KetherParser(["move"], namespace = "adyeshach", shared = true)
private fun actionMove() = combinationParser {
    it.group(
        expects("relative"),
        command("to", then = any()).option(),
        command("x", then = command("to", then = double())).option(),
        command("y", then = command("to", then = double())).option(),
        command("z", then = command("to", then = double())).option(),
    ).apply(it) { relative, to, x, y, z ->
        now {
            val script = script()
            if (script.getManager() == null || !script.isEntitySelected()) {
                errorBy("error-no-manager-or-entity-selected")
            }
            script.getEntities().forEach { e ->
                if (to is Location) {
                    e.moveTarget = to
                } else if (relative != null) {
                    val moveTo = Location(e.world, e.x + (x ?: 0.0), e.y + (y ?: 0.0), e.z + (z ?: 0.0))
                    e.moveTarget = moveTo
                } else {
                    val moveTo = Location(e.world, x ?: e.x, y ?: e.y, z ?: e.z)
                    e.moveTarget = moveTo
                }
            }
        }
    }
}