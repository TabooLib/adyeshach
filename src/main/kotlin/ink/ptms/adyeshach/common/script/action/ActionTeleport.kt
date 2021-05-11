package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.ParsedAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import org.bukkit.Location
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionTeleport(val location: ParsedAction<*>) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return context.newFrame(location).run<Location>().thenAccept { loc ->
            val s = (context.context() as ScriptContext)
            if (s.getManager() == null) {
                throw RuntimeException("No manager selected.")
            }
            if (!s.entitySelected()) {
                throw RuntimeException("No entity selected.")
            }
            s.getEntities()!!.filterNotNull().forEach {
                it.teleport(loc)
            }
        }
    }

    override fun toString(): String {
        return "ActionTeleport(location=$location)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionTeleport(it.next(ArgTypes.ACTION))
        }
    }
}