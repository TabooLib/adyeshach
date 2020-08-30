package ink.ptms.adyeshach.common.script.action.npc

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.script.Kether
import ink.ptms.adyeshach.common.script.ScriptContext
import io.izzel.kether.common.api.*
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.RuntimeException
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author IzzelAliz
 */
class ActionCreate(val id: String, val type: EntityTypes, val location: Location) : QuestAction<Void, ScriptContext> {

    override fun isAsync(): Boolean {
        return false
    }

    override fun process(context: ScriptContext): CompletableFuture<Void> {
        if (context.getManager() == null) {
            throw RuntimeException("No manager selected.")
        }
        context.getManager()!!.create(type, location).id = id
        return CompletableFuture.completedFuture(null)
    }

    override fun getDataPrefix(): String {
        return "create"
    }

    override fun toString(): String {
        return "ActionCreate(id='$id', type=$type, location=$location)"
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parser(): QuestActionParser {
            return object : QuestActionParser {

                override fun <T, C : QuestContext> resolve(resolver: QuestResolver<C>): QuestAction<T, C> {
                    return Function<QuestResolver<C>, QuestAction<T, C>> { t ->
                        val id = t.nextElement()
                        val type = t.nextElement()
                        val entityType = Enums.getIfPresent(EntityTypes::class.java, type.toUpperCase()).orNull() ?: throw RuntimeException("Entity \"$type\" not supported.")
                        var location = Location(Bukkit.getWorlds()[0], 0.0, 0.0, 0.0)
                        if (t.hasNext()) {
                            t.mark()
                            if (t.nextElement() == "at" && t.hasNext()) {
                                location = Kether.toLocation(t.nextElement())
                            } else {
                                t.reset()
                            }
                        }
                        ActionCreate(id, entityType, location) as QuestAction<T, C>
                    }.apply(resolver)
                }

                override fun complete(parms: List<String>): List<String> {
                    return KetherCompleters.seq(KetherCompleters.consume()).apply(parms)
                }
            }
        }
    }
}