package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptService
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function


/**
 * @author IzzelAliz
 */
class ActionStop : QuestAction<Void, ScriptContext> {

    override fun isAsync(): Boolean {
        return true
    }

    override fun isPersist(): Boolean {
        return true
    }

    override fun process(context: ScriptContext): CompletableFuture<Void> {
        ScriptService.terminateQuest(context)
        return CompletableFuture<Void>()
    }

    override fun getDataPrefix(): String {
        return "pause"
    }

    override fun toString(): String {
        return "ActionPause()"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t -> ActionStop() as QuestAction<T, C> }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}