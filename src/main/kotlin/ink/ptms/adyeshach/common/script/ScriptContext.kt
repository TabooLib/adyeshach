package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.manager.Manager
import io.izzel.kether.common.api.AbstractQuestContext
import io.izzel.kether.common.api.Quest
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.util.concurrent.CompletableFuture

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.script.ScriptContext
 *
 * @author sky
 * @since 2021/1/20 10:39 上午
 */
class ScriptContext(service: ScriptService, script: Quest, playerIdentifier: String?) :
    AbstractQuestContext<ScriptContext>(
        service, script,
        playerIdentifier
    ) {

    lateinit var id: String

    var viewer: Player?
        set(value) {
            rootFrame.variables().set("__viewer__", value)
        }
        get() {
            return rootFrame.variables().get<Player?>("__viewer__").orElse(null)
        }

    var currentEvent: Pair<Event, KnownEvent<*>>?
        set(value) {
            rootFrame.variables().set("__current#event__", value)
        }
        get() {
            return rootFrame.variables().get<Pair<Event, KnownEvent<*>>?>("__viewer__").orElse(null)
        }

    var currentListener: CompletableFuture<Void>?
        set(value) {
            rootFrame.variables().set("__current#listener__", value)
        }
        get() {
            return rootFrame.variables().get<CompletableFuture<Void>?>("__viewer__").orElse(null)
        }

    var manager: Manager?
        set(value) {
            rootFrame.variables().set("__manager__", value)
        }
        get() {
            return rootFrame.variables().get<Manager?>("__viewer__").orElse(null)
        }

    var entities: List<EntityInstance?>?
        set(value) {
            rootFrame.variables().set("__entities__", value)
        }
        get() {
            return rootFrame.variables().get<List<EntityInstance?>?>("__viewer__").orElse(null)
        }

    fun entitySelected(): Boolean {
        return entities != null && entities!!.filterNotNull().isNotEmpty()
    }

    override fun createExecutor() = ScriptSchedulerExecutor

    companion object {

        fun create(script: Quest, viewer: Player? = null): ScriptContext {
            return ScriptContext(ScriptService, script, null).also {
                it.viewer = viewer
            }
        }
    }
}