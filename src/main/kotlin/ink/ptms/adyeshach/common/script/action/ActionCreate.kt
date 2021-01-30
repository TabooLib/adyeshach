package ink.ptms.adyeshach.common.script.action

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCreate(val id: String, val type: EntityTypes, val location: Location) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.getManager() == null) {
            throw RuntimeException("No manager selected.")
        }
        s.getManager()!!.create(type, location).id = id
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionCreate(id='$id', type=$type, location=$location)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            val id = it.nextToken()
            val type = it.nextToken()
            val entityType = Enums.getIfPresent(EntityTypes::class.java, type.toUpperCase()).orNull() ?: throw RuntimeException("Entity \"$type\" not supported.")
            var location = Location(Bukkit.getWorlds()[0], 0.0, 0.0, 0.0)
            if (it.hasNext()) {
                it.mark()
                if (it.nextToken() == "at" && it.hasNext()) {
                    location = ScriptHandler.toLocation(it.nextToken())
                } else {
                    it.reset()
                }
            }
            ActionCreate(id, entityType, location)
        }
    }
}