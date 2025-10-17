package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.entity.type.AdyHuman
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 */
class ActionSleeping : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
        }
        script.getEntities().filterIsInstance<AdyHuman>().forEach { it.setSleeping(!it.isSleeping()) }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["sleeping"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            ActionSleeping()
        }
    }
}