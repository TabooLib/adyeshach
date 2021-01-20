package ink.ptms.adyeshach.common.script.action.npc

import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.MultipleType
import io.izzel.kether.common.util.LocalizedException
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionSelect(val value: MultipleType, val byId: Boolean) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.manager == null) {
            throw RuntimeException("No manager selected.")
        }
        return value.process(context).thenAccept {
            s.entities = if (byId) s.manager!!.getEntityById(it.toString()) else listOf(s.manager!!.getEntityByUniqueId(it.toString()))
        }
    }

    override fun toString(): String {
        return "ActionSelect(value='$value', byId=$byId)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            val value = it.nextMultipleType()
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