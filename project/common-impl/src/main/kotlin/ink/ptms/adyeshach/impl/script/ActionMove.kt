package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import org.bukkit.Location
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common5.Coerce
import taboolib.common5.util.parseMillis
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.kether.ParserHolder.option
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@KetherParser(["move"], namespace = "adyeshach", shared = true)
private fun actionMove() = combinationParser {
    it.group(
        expects("relative"),
        command("to", then = any()).option(),
        command("x", then = command("to", then = double())).option(),
        command("y", then = command("to", then = double())).option(),
        command("z", then = command("to", then = double())).option(),
        expects("waiting").option(),
    ).apply(it) { relative, to, x, y, z, waiting ->
        if (waiting != null) {
            future {
                val script = script()
                if (script.getManager() == null || !script.isEntitySelected()) {
                    script.throwUndefinedError()
                }
                val entities = script.getEntities()
                val future = CompletableFuture<Unit>()
                val start = System.currentTimeMillis()
                submitAsync(delay = 1, period = 2) {
                    // 判定超时
                    if (start + TimeUnit.SECONDS.toMillis(60) < System.currentTimeMillis()) {
                        cancel()
                        future.completeExceptionally(RuntimeException("timeout"))
                    }
                    // 判定是否移动完成
                    else if (entities.all { e -> !e.hasTag(StandardTags.IS_MOVING, StandardTags.IS_MOVING_START, StandardTags.IS_PATHFINDING) }) {
                        cancel()
                        future.complete(null)
                    }
                }
                future
            }
        } else {
            now {
                val script = script()
                if (script.getManager() == null || !script.isEntitySelected()) {
                    script.throwUndefinedError()
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
}