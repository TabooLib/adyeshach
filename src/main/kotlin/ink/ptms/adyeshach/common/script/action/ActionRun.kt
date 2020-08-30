package ink.ptms.adyeshach.common.script.action

import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionRun(val value: List<QuestAction<Any, QuestContext>>) : QuestAction<Void, QuestContext> {

    override fun isAsync(): Boolean {
        return value.any { it.isAsync }
    }

    override fun isPersist(): Boolean {
        return value.any { it.isAsync }
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext): CompletableFuture<Void> {
        val futures = ArrayList<CompletableFuture<Any>>()
        value.indices.forEach { i ->
            futures.add(context.runAction(i.toString(), value[i]))
        }
        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    override fun getDataPrefix(): String {
        return "run"
    }

    override fun toString(): String {
        return "ActionRun(value=$value)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        ActionRun(t.nextList() as List<QuestAction<Any, QuestContext>>) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}