package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import taboolib.common5.cdouble
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionLook(val x: ParsedAction<*>, val y: ParsedAction<*>, val z: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            errorBy("error-no-manager-or-entity-selected")
        }
        frame.newFrame(x).run<Any>().thenAccept { x ->
            frame.newFrame(y).run<Any>().thenAccept { y ->
                frame.newFrame(z).run<Any>().thenAccept { z ->
                    script.getEntities().forEach {
                        it.controllerLookAt(x.cdouble, y.cdouble, z.cdouble)
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
            ActionLook(x, y, z)
        }
    }
}