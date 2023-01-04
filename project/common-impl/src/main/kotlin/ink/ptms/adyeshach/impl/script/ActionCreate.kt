package ink.ptms.adyeshach.impl.script

import com.google.common.base.Enums
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.loadError
import ink.ptms.adyeshach.impl.setEntities
import org.bukkit.Location
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCreate(val id: ParsedAction<*>, val type: String, val location: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val manager = frame.script().getManager() ?: errorBy("error-no-manager-selected")
        frame.newFrame(id).run<String>().thenAccept { id ->
            frame.newFrame(location).run<Location>().thenAccept { location ->
                val typeStr = if (type.startsWith("*")) {
                    type.substring(1)
                } else {
                    type
                }
                val type = Enums.getIfPresent(EntityTypes::class.java, typeStr.uppercase()).orNull() ?: throw loadError("Entity \"$type\" not supported.")
                val npc = manager.create(type, location)
                npc.id = id
                frame.script().setEntities(listOf(npc))
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["create"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val id = it.next(ArgTypes.ACTION)
            val type = it.nextToken()
            it.expects("at", "on")
            ActionCreate(id, type, it.next(ArgTypes.ACTION))
        }
    }
}