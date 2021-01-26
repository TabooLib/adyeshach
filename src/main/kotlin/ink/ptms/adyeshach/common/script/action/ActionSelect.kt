package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import ink.ptms.adyeshach.common.script.ScriptHandler.setEntities
import io.izzel.taboolib.kotlin.ketherx.ScriptContext
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import io.izzel.taboolib.kotlin.ketherx.common.api.QuestAction
import io.izzel.taboolib.kotlin.ketherx.common.api.QuestContext
import io.izzel.taboolib.kotlin.ketherx.common.loader.InferType
import io.izzel.taboolib.kotlin.ketherx.common.util.LocalizedException
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionSelect(val value: InferType, val byId: Boolean) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.getManager() == null) {
            throw RuntimeException("No manager selected.")
        }
        return value.process(context).thenAccept {
            s.setEntities(if (byId) s.getManager()!!.getEntityById(it.toString()) else listOf(s.getManager()!!.getEntityByUniqueId(it.toString())))
        }
    }

    override fun toString(): String {
        return "ActionSelect(value='$value', byId=$byId)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            val value = it.nextInferType()
            var byId = true
            if (it.hasNext()) {
                it.mark()
                if (it.nextToken() == "by" && it.hasNext()) {
                    byId = when (val type = it.nextToken().toLowerCase()) {
                        "id" -> true
                        "uniqueid", "uuid" -> false
                        else -> throw LocalizedException.of("unknown-select-type", type)
                    }
                } else {
                    it.reset()
                }
            }
            ActionSelect(value, byId)
        }
    }
}