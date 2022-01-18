package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionRespawn: ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.entitySelected()) {
            error("Manager or Entity is not selected")
        }
        script.getEntities()?.forEach { it?.respawn() }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["respawn"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            ActionRespawn()
        }
    }
}