package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.script.ScriptContext
import io.izzel.kether.common.api.*
import io.izzel.kether.common.util.LocalizedException
import java.lang.RuntimeException
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionSelect(val value: String, val byId: Boolean) : QuestAction<Void, ScriptContext> {

    override fun isAsync(): Boolean {
        return false
    }

    override fun process(context: ScriptContext): CompletableFuture<Void> {
        val manager = context.getManager()
        if (manager !is Manager) {
            throw RuntimeException("No manager selected.")
        }
        context.persistentData["__entity__"] = if (byId) manager.getEntityById(value) else listOf(manager.getEntityByUniqueId(value))
        return CompletableFuture.completedFuture(null)
    }

    override fun getDataPrefix(): String {
        return "select"
    }

    override fun toString(): String {
        return "ActionSelect(value='$value', byId=$byId)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        val manager = t.nextElement()
                        var byId = true
                        if (t.hasNext()) {
                            t.mark()
                            if (t.nextElement() == "by" && t.hasNext()) {
                                byId = when (val type = t.nextElement().toLowerCase()) {
                                    "id" -> true
                                    "uniqueid", "uuid" -> false
                                    else -> throw LocalizedException.of("unknown-select-type", type)
                                }
                            } else {
                                t.reset()
                            }
                        }
                        ActionSelect(manager, byId) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}