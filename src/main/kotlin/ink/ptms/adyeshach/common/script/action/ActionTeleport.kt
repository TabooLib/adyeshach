package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import io.izzel.taboolib.kotlin.ketherx.ScriptContext
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import io.izzel.taboolib.kotlin.ketherx.common.api.QuestAction
import io.izzel.taboolib.kotlin.ketherx.common.api.QuestContext
import org.bukkit.Location
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionTeleport(val location: Location) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.getManager() == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        s.getEntities()!!.filterNotNull().forEach {
            it.teleport(location)
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionTeleport(location=$location)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionTeleport(ScriptHandler.toLocation(it.nextToken()))
        }
    }
}