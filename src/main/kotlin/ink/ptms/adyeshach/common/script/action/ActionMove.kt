package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import org.bukkit.Location
import taboolib.common5.Coerce
import openapi.kether.ArgTypes
import openapi.kether.ParsedAction
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionMove(val x: ParsedAction<*>, val y: ParsedAction<*>, val z: ParsedAction<*>, val relative: Boolean): ScriptAction<Void>() {

    fun Any.d() = Coerce.toDouble(this)

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(x).run<Any>().thenAccept { x ->
            frame.newFrame(y).run<Any>().thenAccept { y ->
                frame.newFrame(z).run<Any>().thenAccept { z ->
                    val s = frame.script()
                    if (s.getManager() == null) {
                        error("No manager selected.")
                    }
                    if (!s.entitySelected()) {
                        error("No entity selected.")
                    }
                    s.getEntities()!!.filterNotNull().forEach {
                        if (relative) {
                            it.controllerMove(Location(it.position.world, it.position.x + x.d(), it.position.y + y.d(), it.position.z + z.d()))
                        } else {
                            it.controllerMove(Location(it.position.world, x.d(), y.d(), z.d()))
                        }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {

        @KetherParser(["meta"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            var relative = false
            it.mark()
            if (it.nextToken() == "relative") {
                relative = true
            } else {
                it.reset()
            }
            var x: ParsedAction<*> = ParsedAction(LiteralAction<Any>(0))
            var y: ParsedAction<*> = ParsedAction(LiteralAction<Any>(0))
            var z: ParsedAction<*> = ParsedAction(LiteralAction<Any>(0))
            while (it.hasNext()) {
                it.mark()
                when (it.nextToken()) {
                    "x" -> {
                        it.expect("to")
                        x = it.next(ArgTypes.ACTION)
                    }
                    "y" -> {
                        it.expect("to")
                        y = it.next(ArgTypes.ACTION)
                    }
                    "z" -> {
                        it.expect("to")
                        z = it.next(ArgTypes.ACTION)
                    }
                    else -> {
                        it.reset()
                        break
                    }
                }
            }
            ActionMove(x, y, z, relative)
        }
    }
}