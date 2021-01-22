package ink.ptms.adyeshach.common.script.action.bukkit

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import io.izzel.kether.common.loader.MultipleType
import io.izzel.taboolib.module.locale.TLocale
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionPlaceholder(val source: MultipleType) : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        return source.process(context).thenApply {
            TLocale.Translate.setPlaceholders((context.context() as ScriptContext).viewer!!, it.toString().trimIndent())
        }
    }

    override fun toString(): String {
        return "ActionPlaceholder(source='$source')"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionPlaceholder(it.nextMultipleType())
        }
    }
}