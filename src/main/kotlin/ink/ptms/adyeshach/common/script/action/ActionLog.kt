package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import io.izzel.kether.common.loader.MultipleType
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionLog(val message: MultipleType) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return message.process(context).thenAccept {
            if (context.context() is ScriptContext) {
                val player = (context.context() as ScriptContext).viewer?.name.toString()
                println("[Adyeshach] ${it.toString().trimIndent().replace("@player", player)}")
            } else {
                println("[Adyeshach] ${it.toString().trimIndent()}")
            }
        }
    }

    override fun toString(): String {
        return "ActionLog{" +
                "message='" + message + '\'' +
                '}'
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionLog(it.nextMultipleType())
        }
    }
}