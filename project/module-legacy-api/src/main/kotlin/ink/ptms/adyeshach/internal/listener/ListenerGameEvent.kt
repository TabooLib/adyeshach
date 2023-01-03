package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.impl.DefaultScriptManager
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.util.resettableLazy
import taboolib.module.configuration.Configuration
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.util.sendLang
import java.io.File

object ListenerGameEvent {

    data class EventFile(val on: String, val run: String?, val runScript: String?)

    val eventMap = ArrayList<EventFile>()

    val reload by resettableLazy {
        init()
    }

    @Awake(LifeCycle.ENABLE)
    fun init() {
        val file = File(getDataFolder(), "event.yml")
        if (file.exists()) {
            eventMap.clear()
            Configuration.loadFromFile(file).getMapList("event").forEach { map ->
                eventMap += EventFile(map["if"].toString(), map["run"]?.toString(), map["run-script"]?.toString())
            }
            info("Loaded ${eventMap.size} event(s).")
        }
    }

    @SubscribeEvent
    private fun onJoin(e: PlayerJoinEvent) {
        invokeEvent(e.player, "join", emptyMap())
    }

    @SubscribeEvent
    private fun onDamage(e: AdyeshachEntityDamageEvent) {
        invokeEvent(e.player, "npc damage", mapOf("id" to e.entity.id, "@entities" to listOf(e.entity)))
    }

    @SubscribeEvent
    private fun onInteract(e: AdyeshachEntityInteractEvent) {
        if (e.isMainHand) {
            invokeEvent(e.player, "npc interact", mapOf("id" to e.entity.id, "@entities" to listOf(e.entity)))
        }
    }

    fun invokeEvent(player: Player, id: String, args: Map<String, Any>) {
        eventMap.filter { it.on == id }.forEach { event ->
            when {
                event.run != null -> {
                    try {
                        KetherShell.eval(event.run, namespace = listOf("adyeshach")) {
                            sender = adaptPlayer(player)
                            args.forEach { (k, v) -> set(k, v) }
                        }
                    } catch (ex: Throwable) {
                        player.sendLang("command-script-error", ex.localizedMessage)
                        ex.printKetherErrorMessage()
                    }
                }
                event.runScript != null -> {
                    val script = DefaultScriptManager.workspace.scripts[event.runScript]
                    if (script != null) {
                        val context = ScriptContext.create(script) {
                            sender = adaptPlayer(player)
                            args.forEach { (k, v) -> set(k, v) }
                        }
                        try {
                            DefaultScriptManager.workspace.runScript("${event.runScript}:${player.name}", context)
                        } catch (ex: Throwable) {
                            player.sendLang("command-script-error", ex.localizedMessage)
                            ex.printKetherErrorMessage()
                        }
                    }
                }
            }
        }
    }
}