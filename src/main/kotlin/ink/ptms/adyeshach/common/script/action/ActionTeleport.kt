package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import org.bukkit.Location
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.platform.util.toBukkitLocation
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionTeleport(val location: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(location).run<taboolib.common.util.Location>().thenAccept {
            val s = frame.script()
            if (s.getManager() == null) {
                error("No manager selected.")
            }
            if (!s.entitySelected()) {
                error("No entity selected.")
            }
            s.getEntities()!!.filterNotNull().forEach { en -> en.teleport(it.toBukkitLocation()) }
        }
    }

    companion object {

        @KetherParser(["teleport"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            ActionTeleport(it.next(ArgTypes.ACTION))
        }
    }
}