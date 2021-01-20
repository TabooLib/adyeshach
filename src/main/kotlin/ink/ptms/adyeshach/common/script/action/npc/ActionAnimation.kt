package ink.ptms.adyeshach.common.script.action.npc

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptParser
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.util.LocalizedException
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionAnimation(val animation: BukkitAnimation) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.manager == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        s.entities!!.filterNotNull().forEach {
            it.displayAnimation(animation)
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionAnimation(animation=$animation)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            val type = it.nextToken()
            val animation = Enums.getIfPresent(BukkitAnimation::class.java, type.toUpperCase()).orNull() ?: throw LocalizedException.of("unknown-animation", type)
            ActionAnimation(animation)
        }
    }
}