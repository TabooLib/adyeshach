package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import org.bukkit.Location
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionMove(val x: ParsedAction<*>, val y: ParsedAction<*>, val z: ParsedAction<*>, val relative: Boolean) : ScriptAction<Void>() {

    fun Any.double() = Coerce.toDouble(this)

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(x).run<Any>().thenAccept { x ->
            frame.newFrame(y).run<Any>().thenAccept { y ->
                frame.newFrame(z).run<Any>().thenAccept { z ->
                    val script = frame.script()
                    if (script.getManager() == null || !script.entitySelected()) {
                        error("Manager or Entity is not selected")
                    }
                    script.getEntities()?.forEach {
                        if (relative) {
                            it?.controllerMove(Location(it.position.world, it.position.x + x.double(), it.position.y + y.double(), it.position.z + z.double()))
                        } else {
                            it?.controllerMove(Location(it.position.world, x.double(), y.double(), z.double()))
                        }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["move"], namespace = "adyeshach", shared = true)
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