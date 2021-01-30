package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.script.ScriptHandler.setManager
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionUse(val manager: String, val temporary: Boolean) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (manager == "private" && s.sender !is Player) {
            throw RuntimeException("The private manager required a player viewer.")
        }
        s.setManager(when (manager) {
            "public" -> {
                if (temporary) {
                    AdyeshachAPI.getEntityManagerPublicTemporary()
                } else {
                    AdyeshachAPI.getEntityManagerPublic()
                }
            }
            "private" -> {
                if (temporary) {
                    AdyeshachAPI.getEntityManagerPrivateTemporary(s.sender as Player)
                } else {
                    AdyeshachAPI.getEntityManagerPrivate(s.sender as Player)
                }
            }
            else -> null
        })
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