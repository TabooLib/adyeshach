package ink.ptms.adyeshach.common.script.action

import com.google.common.base.Enums
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.script.Kether
import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.kether.common.api.*
import io.izzel.kether.common.util.LocalizedException
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.lang.RuntimeException
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionTeleport(val location: Location) : QuestAction<Void, ScriptContext> {

    override fun isAsync(): Boolean {
        return false
    }

    override fun process(context: ScriptContext): CompletableFuture<Void> {
        if (context.getManager() == null) {
            throw LocalizedException.of("runtime-error.no-manager")
        }
        if (!context.entitySelected()) {
            throw LocalizedException.of("runtime-error.no-npc")
        }
        context.getEntity()!!.filterNotNull().forEach {
            it.teleport(location)
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun getDataPrefix(): String {
        return "teleport"
    }

    override fun toString(): String {
        return "ActionTeleport(location=$location)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        ActionTeleport(Kether.toLocation(t.nextElement())) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}