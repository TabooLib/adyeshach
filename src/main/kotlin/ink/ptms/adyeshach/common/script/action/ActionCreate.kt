package ink.ptms.adyeshach.common.script.action

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import ink.ptms.adyeshach.common.script.ScriptHandler.loadError
import org.bukkit.Location
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCreate(val id: String, val type: EntityTypes, val location: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val manager = frame.script().getManager() ?: error("No manager selected.")
        return frame.newFrame(location).run<Location>().thenAccept {
            manager.create(type, it).id = id
        }
    }

    internal object Parser {

        @KetherParser(["create"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val id = it.nextToken()
            val type = it.nextToken()
            val entityType = Enums.getIfPresent(EntityTypes::class.java, type.uppercase()).orNull() ?: throw loadError("Entity \"$type\" not supported.")
            it.expects("at", "on")
            ActionCreate(id, entityType, it.next(ArgTypes.ACTION))
        }
    }
}