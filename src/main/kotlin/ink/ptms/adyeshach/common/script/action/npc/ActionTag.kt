package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import io.izzel.kether.common.api.*
import io.izzel.kether.common.util.LocalizedException
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionTag(val key: String, val symbol: Symbol, val value: String?) : QuestAction<Any?, ScriptContext> {

    enum class Symbol {

        GET, SET, REMOVE, HAS
    }

    override fun isAsync(): Boolean {
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: ScriptContext): CompletableFuture<Any?> {
        if (context.getManager() == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!context.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        return when (symbol) {
            Symbol.REMOVE -> {
                context.getEntity()!!.filterNotNull().forEach {
                    it.removeTag(key)
                }
                CompletableFuture.completedFuture(null)
            }
            Symbol.SET -> {
                context.getEntity()!!.filterNotNull().forEach {
                    if (value!! == "null") {
                        it.removeTag(key)
                    } else {
                        it.setTag(key, value)
                    }
                }
                CompletableFuture.completedFuture(null)
            }
            Symbol.HAS -> {
                CompletableFuture.completedFuture(context.getEntity()!!.filterNotNull().firstOrNull()?.hasTag(key))
            }
            else -> {
                CompletableFuture.completedFuture(context.getEntity()!!.filterNotNull().firstOrNull()?.getTag(key))
            }
        }
    }

    override fun getDataPrefix(): String {
        return "tag"
    }

    override fun toString(): String {
        return "ActionTag(key='$key', symbol=$symbol, value=$value)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        val symbol = when (val type = t.nextElement()) {
                            "set" -> Symbol.SET
                            "get" -> Symbol.GET
                            "has" -> Symbol.HAS
                            "remove" -> Symbol.REMOVE
                            else -> throw LocalizedException.of("not-tag-method", type)
                        }
                        val key = t.nextElement()
                        val value = if (symbol == Symbol.SET) {
                            t.consume("to")
                            t.nextElement()
                        } else null
                        ActionTag(key, symbol, value) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}