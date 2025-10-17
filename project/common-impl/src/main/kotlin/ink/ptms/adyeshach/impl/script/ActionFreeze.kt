package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.common5.cbool
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 */
class ActionFreeze(val value: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
        }
        frame.newFrame(value).run<Any>().thenAccept { value ->
            script.getEntities().forEach { it.isFreeze = value.cbool }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["freeze"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            ActionFreeze(it.next(ArgTypes.ACTION))
        }
    }
}