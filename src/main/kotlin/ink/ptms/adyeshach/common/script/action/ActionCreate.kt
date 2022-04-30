package ink.ptms.adyeshach.common.script.action

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import ink.ptms.adyeshach.common.script.ScriptHandler.loadError
import org.bukkit.Location
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.platform.util.toBukkitLocation
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCreate(val id: ParsedAction<*>, val type: ParsedAction<*>, val location: ParsedAction<*>): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val manager = frame.script().getManager() ?: error("Manager is not selected")
        return frame.newFrame(id).run<String>().thenAccept { id ->
            frame.newFrame(type).run<String>().thenAccept { type ->
                frame.newFrame(location).run<Location>().thenAccept { location ->
                    manager.create(
                        Enums.getIfPresent(EntityTypes::class.java, type.uppercase()).orNull() ?: throw loadError("Entity \"$type\" not supported."),
                        location
                    ).id = id
                }.join()
            }.join()
        }
    }

    companion object {

        @KetherParser(["create"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val id = it.next(ArgTypes.ACTION)
            val type = it.next(ArgTypes.ACTION)
            it.expects("at", "on")
            ActionCreate(id, type, it.next(ArgTypes.ACTION))
        }
    }
}