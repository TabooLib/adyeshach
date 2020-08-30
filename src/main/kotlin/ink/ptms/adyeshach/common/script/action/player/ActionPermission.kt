package ink.ptms.adyeshach.common.script.action.player

import ink.ptms.adyeshach.common.script.ScriptContext
import io.izzel.kether.common.api.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionPermission(val permission: String) : QuestAction<Boolean, ScriptContext> {

    override fun isAsync(): Boolean {
        return false
    }

    override fun process(context: ScriptContext): CompletableFuture<Boolean> {
        if (context.viewer == null) {
            throw RuntimeException("This action required any viewer.")
        }
        return CompletableFuture.completedFuture(context.viewer?.hasPermission(permission))
    }

    override fun getDataPrefix(): String {
        return "permission"
    }

    override fun toString(): String {
        return "ActionPermission(permission='$permission')"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t -> ActionPermission(t.nextElement()) as QuestAction<T, C> }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}