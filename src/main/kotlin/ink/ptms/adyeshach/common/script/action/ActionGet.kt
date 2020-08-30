package ink.ptms.adyeshach.common.script.action

import io.izzel.kether.common.api.*
import io.izzel.kether.common.util.Coerce
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionGet(val key: String) : QuestAction<Any?, QuestContext> {

    override fun isAsync(): Boolean {
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext): CompletableFuture<Any?> {
        return CompletableFuture.completedFuture(context.persistentData[key])
    }

    override fun getDataPrefix(): String {
        return "get"
    }

    override fun toString(): String {
        return "ActionGet(key='$key')"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t -> ActionGet(t.nextElement()) as QuestAction<T, C> }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}