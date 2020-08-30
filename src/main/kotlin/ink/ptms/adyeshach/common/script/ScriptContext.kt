package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.manager.Manager
import io.izzel.kether.common.api.*
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.collections.ArrayList


/**
 * @Author IzzelAliz
 */
class ScriptContext(
        service: QuestService<*>?,
        parent: AbstractQuestContext?,
        quest: Quest?,
        playerIdentifier: String?,
        runningBlock: String?,
        index: Int,
        dataKey: String?,
        tempData: Map<String, Any>?,
        persistentData: Map<String, Any>,
        childKey: String?,
        anonymous: Boolean,
        var viewer: Player?
) : PersistentQuestContext(service, parent, quest, playerIdentifier, runningBlock, index, dataKey, tempData, persistentData, childKey, anonymous) {

    var currentEvent: Pair<Event, KnownEvent<*>>? = null
    var currentListener: CompletableFuture<Void>? = null

    override fun createExecutor(): Executor {
        return ScriptSchedulerExecutor
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : QuestContext> createChild(key: String, anonymous: Boolean): C {
        val context = ScriptContext(service, this, quest, playerIdentifier, key, 0, QuestContext.BASE_DATA_KEY, null, persistentData, key, anonymous, viewer)
        children.addLast(context)
        return context as C
    }

    @Suppress("UNCHECKED_CAST")
    fun getManager(): Manager? {
        return (persistentData["__manager__"] ?: return null) as Manager
    }

    @Suppress("UNCHECKED_CAST")
    fun getEntity(): List<EntityInstance?>? {
        return (persistentData["__entity__"] ?: return null) as List<EntityInstance?>
    }

    fun entitySelected(): Boolean {
        return getEntity() != null && getEntity()!!.filterNotNull().isNotEmpty()
    }

    companion object {

        fun create(quest: Quest, viewer: Player? = null): ScriptContext {
            return ScriptContext(
                    ScriptService,
                    null,
                    quest,
                    null,
                    QuestContext.BASE_BLOCK,
                    0,
                    QuestContext.BASE_DATA_KEY,
                    null,
                    HashMap(),
                    null,
                    false,
                    viewer
            )
        }
    }
}