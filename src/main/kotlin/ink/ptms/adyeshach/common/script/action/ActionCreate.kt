package ink.ptms.adyeshach.common.script.action

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.common.api.ParsedAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestAction
import io.izzel.taboolib.kotlin.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.script
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCreate(val id: String, val type: EntityTypes, val location: ParsedAction<*>) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val manager = context.script().getManager() ?: error("No manager selected.")
        return context.newFrame(location).run<Location>().thenAccept {
            manager.create(type, it).id = id
        }
    }

    override fun toString(): String {
        return "ActionCreate(id='$id', type=$type, location=$location)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            val id = it.nextToken()
            val type = it.nextToken()
            val entityType = Enums.getIfPresent(EntityTypes::class.java, type.toUpperCase()).orNull() ?: error("Entity \"$type\" not supported.")
            it.expects("at", "on")
            ActionCreate(id, entityType, it.next(ArgTypes.ACTION))
        }
    }
}