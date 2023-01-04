package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionStill: ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            errorBy("error-no-manager-or-entity-selected")
        }
        script.getEntities().forEach { it.moveFrames = null }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["still"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            ActionStill()
        }
    }
}