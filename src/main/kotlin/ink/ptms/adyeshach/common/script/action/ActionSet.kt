package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptResolver
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionSet(val key: String, val value: Any) : QuestAction<Void, QuestContext> {

    override fun isAsync(): Boolean {
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext): CompletableFuture<Void> {
        when (value) {
            is QuestAction<*, *> -> {
                CompletableFuture<Void>().also { future ->
                    context.runAction("set", value).thenAccept {
                        context.persistentData[key] = it
                        future.complete(null)
                    }
                }
            }
            "null" -> {
                context.persistentData.remove(key)
            }
            else -> {
                context.persistentData[key] = value
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun getDataPrefix(): String {
        return "set"
    }

    override fun toString(): String {
        return "ActionSet(key='$key', value=$value)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        val key = t.nextElement()
                        t.consume("to")
                        val value = try {
                            t.mark()
                            t.nextAction<QuestContext>()
                        } catch (ignore: Throwable) {
                            t.reset()
                            (t as ScriptResolver<C>).nextAny()
                        }
                        ActionSet(key, value) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}