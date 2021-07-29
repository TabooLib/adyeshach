package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionSleeping: ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val s = frame.script()
        if (s.getManager() == null) {
            error("No manager selected.")
        }
        if (!s.entitySelected()) {
            error("No entity selected.")
        }
        s.getEntities()!!.filterNotNull().forEach {
            if (it is AdyHuman) {
                it.setSleeping(!it.isSleeping())
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {

        @KetherParser(["sleeping"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            ActionSleeping()
        }
    }
}