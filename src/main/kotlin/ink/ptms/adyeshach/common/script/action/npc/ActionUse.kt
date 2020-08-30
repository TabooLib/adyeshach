package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.script.ScriptContext
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionUse(val manager: String, val temporary: Boolean) : QuestAction<Void, ScriptContext> {

    override fun isAsync(): Boolean {
        return false
    }

    override fun process(context: ScriptContext): CompletableFuture<Void> {
        if (manager == "private" && context.viewer == null) {
            throw RuntimeException("The private manager required any viewer.")
        }
        context.persistentData["__manager__"] = when (manager) {
            "public" -> if (temporary) AdyeshachAPI.getEntityManagerPublicTemporary() else AdyeshachAPI.getEntityManagerPublic()
            "private" -> if (temporary) AdyeshachAPI.getEntityManagerPrivateTemporary(context.viewer!!) else AdyeshachAPI.getEntityManagerPrivate(context.viewer!!)
            else -> null
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun getDataPrefix(): String {
        return "use"
    }

    override fun toString(): String {
        return "ActionUse(manager='$manager', temporary=$temporary)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        val manager = t.nextElement()
                        var temporary = false
                        if (t.hasNext()) {
                            t.mark()
                            if (t.nextElement() == "temporary") {
                                temporary = true
                            } else {
                                t.reset()
                            }
                        }
                        ActionUse(manager, temporary) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}