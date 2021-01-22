package ink.ptms.adyeshach.common.script.action.bukkit

import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import io.izzel.kether.common.loader.MultipleType
import io.izzel.taboolib.module.locale.TLocale
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionColor(val source: MultipleType) : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        return source.process(context).thenApply {
            TLocale.Translate.setColored(it.toString().trimIndent())
        }
    }

    override fun toString(): String {
        return "ActionColor(source='$source')"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionColor(it.nextMultipleType())
        }
    }
}