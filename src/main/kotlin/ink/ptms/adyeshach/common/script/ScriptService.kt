package ink.ptms.adyeshach.common.script

import com.google.common.collect.*
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.script.util.Closables
import io.izzel.kether.common.api.*
import io.izzel.kether.common.api.data.ExitStatus
import io.izzel.kether.common.loader.SimpleQuestLoader
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Coerce
import io.izzel.taboolib.util.Files
import org.bukkit.Bukkit
import org.bukkit.event.Event
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * @author IzzelAliz
 */
@Suppress("UnstableApiUsage")
object ScriptService : QuestService<ScriptContext> {

    private val registry = DefaultRegistry()
    private val syncExecutor = ScriptSchedulerExecutor
    private val asyncExecutor = Executors.newScheduledThreadPool(2)
    private val runningScripts = MultimapBuilder.hashKeys().arrayListValues().build<String, ScriptContext>()

    private var settings = HashMap<String, Map<String, Any>>()
    private var scripts = HashMap<String, Quest>()

    private var listener = ArrayList<AutoCloseable>()

    @Suppress("UNCHECKED_CAST")
    fun loadAll() {
        listener.forEach { it.close() }
        listener.clear()

        loadScripts()
        loadSettings()

        scripts.forEach {
            if (Coerce.toBoolean(getQuestSettings(it.value.id)["autostart"])) {
                startQuest(ScriptContext.create(it.value))
                return@forEach
            }
            val trigger = getQuestSettings(it.value.id)["start"] ?: return@forEach
            if (ScriptHandler.getKnownEvent(trigger.toString()) != null) {
                val event = ScriptHandler.getKnownEvent(trigger.toString()) as KnownEvent<Event>
                listener.add(Closables.listening(event.eventClass.java) { e ->
                    val context = ScriptContext.create(it.value)
                    val player = event.field["player"]
                    val id = if (player != null) {
                        context.viewer = Bukkit.getPlayerExact(player.first.func(e).toString())
                        "${it.value.id}:${context.viewer?.name}"
                    } else {
                        "${it.value.id}:$trigger"
                    }
                    context.currentEvent = e to event
                    startQuest(id, context)
                })
            } else {
                println("[Adyeshach] Unknown starting trigger $trigger")
            }
        }
    }

    fun loadSettings() {
        settings.clear()
        scripts.values.forEach { quest ->
            val context = ScriptContext.create(quest)
            quest.getBlock("settings").ifPresent {
                it.actions.forEach { action ->
                    action.process(context.rootFrame())
                }
            }
            settings[quest.id] = context.rootFrame().variables().values().map { it.key to it.value }.toMap()
        }
    }

    fun loadScripts() {
        scripts.clear()
        val questLoader = SimpleQuestLoader()
        val folder = Files.folder(Adyeshach.plugin.dataFolder, "npc/script").toPath()
        val scriptMap = HashMap<String, Quest>()
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
                        val bytes = Files.readFromFile(path.toFile())?.toByteArray(StandardCharsets.UTF_8) ?: ByteArray(0)
                        scriptMap[name] = questLoader.load(this, Adyeshach.plugin.logger, name, bytes)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        scripts.putAll(scriptMap)
    }

    fun cancelAll() {
        getRunningQuestContext().forEach { terminateQuest(it) }
    }

    fun getRunningQuestContext(): List<ScriptContext> {
        return ImmutableList.copyOf(runningScripts.values())
    }

    fun startQuest(id: String, context: ScriptContext) {
        context.id = id
        runningScripts.put(id, context)
        context.runActions().thenRunAsync({
            runningScripts.remove(id, context)
        }, this.executor)
    }

    override fun getRegistry(): QuestRegistry {
        return registry
    }

    override fun getQuest(id: String): Optional<Quest> {
        return Optional.ofNullable(scripts[id])
    }

    override fun getQuestSettings(id: String): Map<String, Any> {
        return Collections.unmodifiableMap(settings.getOrDefault(id, ImmutableMap.of()))
    }

    override fun getQuests(): Map<String, Quest> {
        return Collections.unmodifiableMap(scripts)
    }

    override fun getRunningQuests(): Multimap<String, ScriptContext> {
        return runningScripts
    }

    override fun getRunningQuests(playerIdentifier: String): List<ScriptContext> {
        return Collections.unmodifiableList(runningScripts[playerIdentifier])
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
        return ScriptHandler.storage!!
    }

    override fun startQuest(context: ScriptContext) {
        startQuest(UUID.randomUUID().toString(), context)
    }

    override fun terminateQuest(context: ScriptContext) {
        if (!context.exitStatus.isPresent) {
            context.setExitStatus(ExitStatus.paused())
            runningScripts.remove(context.id, context)
        }
    }
}