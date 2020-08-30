package ink.ptms.adyeshach.common.script

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.script.util.Closables
import io.izzel.kether.common.api.*
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Files
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.io.File
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.collections.ArrayList


/**
 * @author IzzelAliz
 */
@Suppress("UnstableApiUsage")
object ScriptService : QuestService<ScriptContext> {

    private val registry: QuestRegistry = DefaultRegistry()
    private val syncExecutor: Executor = ScriptSchedulerExecutor
    private val asyncExecutor = Executors.newScheduledThreadPool(2)
    private val runningQuests = MultimapBuilder.hashKeys().arrayListValues().build<String, ScriptContext>()
    private var questMap: Map<String, Quest>? = null
    private var settingsMap: MutableMap<String, Map<String, Any>>? = null
    private var listener = ArrayList<AutoCloseable>()

    @Suppress("UNCHECKED_CAST")
    fun loadAll() {
        listener.forEach { it.close() }
        listener.clear()
        questMap = load()
        settingsMap = HashMap()
        for (quest in questMap!!.values) {
            val context = SettingsContext(this, quest)
            context.runActions().join()
            settingsMap!![quest.id] = context.persistentData
        }
        questMap!!.forEach {
            val trigger = getQuestSettings(it.value.id)["start"].toString()
            if (trigger == "start") {
                startQuest(ScriptContext.create(it.value))
            } else {
                val event = (Kether.getKnownEvent(trigger) ?: throw LocalizedException.of("unknown-event", trigger)) as KnownEvent<Event>
                listener.add(Closables.listening(event.eventClass.java) { e ->
                    val context = ScriptContext.create(it.value)
                    val player = event.field["player"]
                    val id = if (player != null) {
                        context.viewer = Bukkit.getPlayerExact(player.key.invoke(e).toString())
                        "${it.value.id}:${context.viewer?.name}"
                    } else {
                        "${it.value.id}:$trigger"
                    }
                    context.persistentData["\$currentEvent"] = e to event
                    startQuest(id, context)
                })
            }
        }
    }

    fun load(): Map<String, Quest> {
        val folder = Files.folder(Adyeshach.plugin.dataFolder, "npc/script").toPath()
        val questMap = HashMap<String, Quest>()
        if (java.nio.file.Files.notExists(folder)) {
            java.nio.file.Files.createDirectories(folder)
        }
        val iterator = java.nio.file.Files.walk(folder).iterator()
        while (iterator.hasNext()) {
            val path = iterator.next()
            if (!java.nio.file.Files.isDirectory(path)) {
                try {
                    val name = folder.relativize(path).toString().replace(File.separatorChar, '.')
                    if (name.endsWith(".ady")) {
                        questMap[name] = ScriptLoader.of(path).load(this, Adyeshach.plugin.logger, name)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return questMap
    }

    fun cancelAll() {
        getRunningQuestContext().forEach { terminateQuest(it) }
    }

    fun getRunningQuestContext(): List<ScriptContext> {
        return ImmutableList.copyOf(runningQuests.values())
    }

    fun startQuest(id: String, context: ScriptContext) {
        runningQuests.put(id, context)
        context.runActions().thenRunAsync(Runnable {
            runningQuests.remove(id, context)
        }, this.executor)
    }

    override fun getRegistry(): QuestRegistry {
        return registry
    }

    override fun getQuest(id: String): Optional<Quest> {
        return Optional.ofNullable(questMap!![id])
    }

    override fun getQuestSettings(id: String): Map<String, Any> {
        return Collections.unmodifiableMap(settingsMap!!.getOrDefault(id, ImmutableMap.of()))
    }

    override fun getQuests(): Map<String, Quest> {
        return Collections.unmodifiableMap(questMap!!)
    }

    override fun startQuest(context: ScriptContext) {
        startQuest(UUID.randomUUID().toString(), context)
    }

    override fun terminateQuest(context: ScriptContext) {
        if (!context.exitStatus.isPresent) {
            context.setExitStatus(ExitStatus.paused())
        }
    }

    override fun getRunningQuests(): Multimap<String, ScriptContext> {
        return runningQuests
    }

    override fun getRunningQuests(playerIdentifier: String): List<ScriptContext> {
        return Collections.unmodifiableList(runningQuests[playerIdentifier])
    }

    override fun getExecutor(): Executor {
        return syncExecutor
    }

    override fun getAsyncExecutor(): ScheduledExecutorService {
        return asyncExecutor
    }

    override fun getLocalizedText(node: String, vararg params: Any): String {
        return TLocale.asString(node, *params)
    }

    override fun getStorage(): QuestStorage {
        return Kether.storage!!
    }
}