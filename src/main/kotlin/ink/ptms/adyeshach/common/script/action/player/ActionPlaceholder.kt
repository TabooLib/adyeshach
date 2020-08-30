package ink.ptms.adyeshach.common.script.action.player

import ink.ptms.adyeshach.common.script.ScriptContext
import io.izzel.kether.common.api.*
import io.izzel.taboolib.module.locale.TLocale
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionPlaceholder(val placeholder: String) : QuestAction<String, ScriptContext> {

    override fun isAsync(): Boolean {
        return false
    }

    override fun process(context: ScriptContext): CompletableFuture<String> {
        if (context.viewer == null) {
            throw RuntimeException("This action required any viewer.")
        }
        return CompletableFuture.completedFuture(TLocale.Translate.setPlaceholders(context.viewer!!, placeholder))
    }

    override fun getDataPrefix(): String {
        return "placeholder"
    }

    override fun toString(): String {
        return "ActionPlaceholder(placeholder='$placeholder')"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t -> ActionPlaceholder(t.nextElement()) as QuestAction<T, C> }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}