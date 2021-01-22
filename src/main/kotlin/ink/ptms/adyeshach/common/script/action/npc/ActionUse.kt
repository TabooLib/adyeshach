package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionUse(val manager: String, val temporary: Boolean) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (manager == "private" && s.viewer !is Player) {
            throw RuntimeException("The private manager required a player viewer.")
        }
        s.manager = when (manager) {
            "public" -> {
                if (temporary) {
                    AdyeshachAPI.getEntityManagerPublicTemporary()
                } else {
                    AdyeshachAPI.getEntityManagerPublic()
                }
            }
            "private" -> {
                if (temporary) {
                    AdyeshachAPI.getEntityManagerPrivateTemporary(s.viewer as Player)
                } else {
                    AdyeshachAPI.getEntityManagerPrivate(s.viewer as Player)
                }
            }
            else -> null
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionUse(manager='$manager', temporary=$temporary)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            val manager = it.nextToken()
            var temporary = false
            if (it.hasNext()) {
                it.mark()
                if (it.nextToken() in listOf("temp", "temporary")) {
                    temporary = true
                } else {
                    it.reset()
                }
            }
            ActionUse(manager, temporary)
        }
    }
}