package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.common.script.ScriptHandler
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.util.sendLang

object ListenerGameEvent {

    data class EventFile(val on: String, val run: String?, val runScript: String?)

    @Config("event.yml")
    lateinit var conf: Configuration
        private set

    val eventMap = ArrayList<EventFile>()

    @Awake(LifeCycle.ENABLE)
    fun init() {
        eventMap.clear()
        conf.reload()
        conf.getMapList("event").forEach { map ->
            eventMap += EventFile(map["if"].toString(), map["run"]?.toString(), map["run-script"]?.toString())
        }
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        invokeEvent(e.player, "join", emptyMap())
    }

    @SubscribeEvent
    fun e(e: AdyeshachEntityDamageEvent) {
        invokeEvent(e.player, "npc damage", mapOf("id" to e.entity.id, "@entities" to listOf(e.entity)))
    }

    @SubscribeEvent
    fun e(e: AdyeshachEntityInteractEvent) {
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
                            rootFrame().variables().also { args.forEach { (k, v) -> it[k] = v } }
                        }
                    } catch (t: Throwable) {
                        player.sendLang("command-script-error", t.localizedMessage)
                        t.printKetherErrorMessage()
                    }
                }
                event.runScript != null -> {
                    val script = ScriptHandler.workspace.scripts[event.runScript]
                    if (script != null) {
                        val context = ScriptContext.create(script) {
                            sender = adaptPlayer(player)
                            rootFrame().variables().also { args.forEach { (k, v) -> it[k] = v } }
                        }
                        try {
                            ScriptHandler.workspace.runScript("${event.runScript}:${player.name}", context)
                        } catch (t: Throwable) {
                            player.sendLang("command-script-error", t.localizedMessage)
                            t.printKetherErrorMessage()
                        }
                    }
                }
            }
        }
    }
}