package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import org.bukkit.Location
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionLook(val x: ParsedAction<*>, val y: ParsedAction<*>, val z: ParsedAction<*>, val smooth: Boolean): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.entitySelected()) {
            error("Manager or Entity is not selected")
        }
        frame.newFrame(x).run<Any>().thenAccept { x ->
            frame.newFrame(y).run<Any>().thenAccept { y ->
                frame.newFrame(z).run<Any>().thenAccept { z ->
                    script.getEntities()?.forEach {
                        it?.controllerLook(Location(it.position.world, Coerce.toDouble(x), Coerce.toDouble(y), Coerce.toDouble(z)), smooth)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["look"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            var smooth = false
            it.mark()
            if (it.nextToken() == "smooth") {
                smooth = true
            } else {
                it.reset()
            }
            var x = literalAction(0)
            var y = literalAction(0)
            var z = literalAction(0)
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