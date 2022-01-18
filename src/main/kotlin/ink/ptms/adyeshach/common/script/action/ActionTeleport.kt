package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import org.bukkit.Location
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionTeleport(val location: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(location).run<Location>().thenAccept {
            val script = frame.script()
            if (script.getManager() == null || !script.entitySelected()) {
                error("Manager or Entity is not selected")
            }
            script.getEntities()?.forEach { en -> en?.teleport(it) }
        }
    }

    companion object {

        @KetherParser(["teleport"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            ActionTeleport(it.next(ArgTypes.ACTION))
        }
    }
}