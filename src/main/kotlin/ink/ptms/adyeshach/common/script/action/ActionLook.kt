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
class ActionLook(val x: ParsedAction<*>, val y: ParsedAction<*>, val z: ParsedAction<*>, val smooth: Boolean): ScriptAction<Void>() {

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
                        it.controllerLook(Location(it.position.world, Coerce.toDouble(x), Coerce.toDouble(y), Coerce.toDouble(z)), smooth)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {

        @KetherParser(["look"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            var smooth = false
            it.mark()
            if (it.nextToken() == "smooth") {
                smooth = true
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
            ActionLook(x, y, z, smooth)
        }
    }
}