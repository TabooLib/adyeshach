package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptResolver
import ink.ptms.adyeshach.common.script.action.ActionCheck.Symbol.*
import io.izzel.kether.common.api.*
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.cronus.util.StringNumber
import io.izzel.taboolib.util.Coerce
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionCheck(val left: Any, val right: Any, val symbol: Symbol) : QuestAction<Boolean, QuestContext> {

    enum class Symbol {

        EQUALS, GT, GT_EQ, LT, LT_EQ
    }

    fun check(left: Any?, right: Any?): Boolean {
        return when (symbol) {
            EQUALS -> left == right
            GT -> Coerce.toDouble(left) > Coerce.toDouble(right)
            GT_EQ -> Coerce.toDouble(left) >= Coerce.toDouble(right)
            LT -> Coerce.toDouble(left) < Coerce.toDouble(right)
            LT_EQ -> Coerce.toDouble(left) <= Coerce.toDouble(right)
        }
    }

    override fun isAsync(): Boolean {
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext): CompletableFuture<Boolean> {
        return CompletableFuture<Boolean>().also { future ->
            if (left is QuestAction<*, *>) {
                context.runAction("equals_left", left).thenAccept { valueLeft ->
                    if (right is QuestAction<*, *>) {
                        context.runAction("equals_right", right).thenAccept { valueRight ->
                            future.complete(check(valueLeft, valueRight))
                        }
                    } else {
                        future.complete(check(valueLeft, right))
                    }
                }
            } else {
                if (right is QuestAction<*, *>) {
                    context.runAction("equals_right", right).thenAccept { valueRight ->
                        future.complete(check(left, valueRight))
                    }
                } else {
                    future.complete(check(left, right))
                }
            }
        }
    }

    override fun getDataPrefix(): String {
        return "check"
    }

    override fun toString(): String {
        return "ActionCheck(left=$left, right=$right)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        val left = try {
                            t.mark()
                            t.nextAction<QuestContext>()
                        } catch (ignore: Throwable) {
                            t.reset()
                            (t as ScriptResolver<C>).nextAny()
                        }
                        val symbol = when (val type = t.nextElement()) {
                            "is", "eq", "equals", "==" -> EQUALS
                            "gt", ">" -> GT
                            "gteq", ">=" -> GT_EQ
                            "lt", "<" -> LT
                            "lteq", "<=" -> LT_EQ
                            else -> throw LocalizedException.of("not-symbol", type)
                        }
                        val right = try {
                            t.mark()
                            t.nextAction<QuestContext>()
                        } catch (ignore: Throwable) {
                            t.reset()
                            (t as ScriptResolver<C>).nextAny()
                        }
                        ActionCheck(left, right, symbol) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}