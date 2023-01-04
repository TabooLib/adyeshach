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
class ActionPosition : ScriptAction<Any>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Any> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            errorBy("error-no-manager-or-entity-selected")
        }
        val e = script.getEntities()
        return CompletableFuture.completedFuture(if (e.size > 1) e.map { it.getLocation() } else e.first().getLocation())
    }

    companion object {

        /**
         * npc position
         */
        @KetherParser(["npc"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("position", "location") {
                    ActionPosition()
                }
            }
        }
    }
}