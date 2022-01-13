package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import org.bukkit.Location
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionPosition: ScriptAction<Location>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Location> {
        val s = frame.script()
        if (s.getManager() == null) {
            error("No manager selected.")
        }
        if (!s.entitySelected()) {
            error("No entity selected.")
        }
        return CompletableFuture.completedFuture(s.getEntities()!!.firstOrNull()!!.getLocation())
    }

    companion object {

        @KetherParser(["position"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            ActionPosition()
        }
    }
}