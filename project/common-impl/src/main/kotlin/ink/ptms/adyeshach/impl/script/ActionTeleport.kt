package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.event.AdyeshachScriptEvent
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.util.isPlayer
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 */
class ActionTeleport(val location: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(location).run<Location>().thenAccept {
            val script = frame.script()
            if (script.getManager() == null || !script.isEntitySelected()) {
                script.throwUndefinedError()
            }
            val entities = script.getEntities()
            val sender = if (script.sender?.isPlayer() == true) script.sender!!.cast<Player>() else null
            if (AdyeshachScriptEvent.Teleport(entities, sender, it).call()) {
                entities.forEach { e -> e.teleport(it) }
            }
        }
    }

    companion object {

        @KetherParser(["teleport"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            ActionTeleport(it.next(ArgTypes.ACTION))
        }
    }
}