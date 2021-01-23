package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptParser
import ink.ptms.adyeshach.common.script.action.ActionCheck.Symbol.*
import io.izzel.kether.common.api.*
import io.izzel.kether.common.loader.MultipleType
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.util.Coerce
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCheck(val left: MultipleType, val right: MultipleType, val symbol: Symbol) : QuestAction<Boolean>() {

    enum class Symbol {

        EQUALS, EQUALS_MEMORY, EQUALS_IGNORE_CASE, GT, GT_EQ, LT, LT_EQ
    }

    fun check(left: Any, right: Any): Boolean {
        return when (symbol) {
            EQUALS -> left == right
            EQUALS_MEMORY -> left === right
            EQUALS_IGNORE_CASE -> left.toString().equals(right.toString(), ignoreCase = true)
            GT -> Coerce.toDouble(left) > Coerce.toDouble(right)
            GT_EQ -> Coerce.toDouble(left) >= Coerce.toDouble(right)
            LT -> Coerce.toDouble(left) < Coerce.toDouble(right)
            LT_EQ -> Coerce.toDouble(left) <= Coerce.toDouble(right)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext.Frame): CompletableFuture<Boolean> {
        return CompletableFuture<Boolean>().also { future ->
            left.process(context).thenAccept { left ->
                right.process(context).thenAccept { right ->
                    future.complete(check(left, right))
                }
            }
        }
    }

    override fun toString(): String {
        return "ActionCheck(left=$left, right=$right)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            val left = it.nextMultipleType()
            val symbol = when (val type = it.nextToken()) {
                "==", "is" -> EQUALS
                "=!", "is!" -> EQUALS_MEMORY
                "=?", "is?" -> EQUALS_IGNORE_CASE
                ">" -> GT
                ">=" -> GT_EQ
                "<" -> LT
                "<=" -> LT_EQ
                else -> throw LocalizedException.of("not-symbol", type)
            }
            val right = it.nextMultipleType()
            ActionCheck(left, right, symbol)
        }
    }
}