package ink.ptms.adyeshach.common.script.action.player

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.*
import io.izzel.kether.common.loader.MultipleType
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionPermission(val permission: MultipleType) : QuestAction<Boolean>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Boolean> {
        return permission.process(context).thenApply {
            (context.context() as ScriptContext).viewer!!.hasPermission(it.toString().trimIndent())
        }
    }

    override fun toString(): String {
        return "ActionPermission(permission='$permission')"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionPermission(it.nextMultipleType())
        }
    }
}