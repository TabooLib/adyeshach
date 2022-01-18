package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionFreeze(val value: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.entitySelected()) {
            error("Manager or Entity is not selected")
        }
        frame.newFrame(value).run<Any>().thenAccept { value ->
            script.getEntities()?.forEach { it?.isFreeze = Coerce.toBoolean(value) }
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