package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerLookAtPlayer
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerRandomLookaround
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerRandomStrollLand
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.script.action.*
import ink.ptms.adyeshach.common.script.action.npc.*
import ink.ptms.adyeshach.common.script.action.player.ActionPermission
import ink.ptms.adyeshach.common.script.action.player.ActionPlaceholder
import io.izzel.kether.common.actions.KetherTypes
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.api.QuestStorage
import io.izzel.kether.common.api.storage.LocalYamlStorage
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.nms.impl.Position
import io.izzel.taboolib.util.Coerce
import io.izzel.taboolib.util.Files
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.EulerAngle
import java.util.concurrent.ConcurrentHashMap

object Kether {

    var storage: QuestStorage? = null
        private set

    val knownEvents = ConcurrentHashMap<String, KnownEvent<*>>()
    val knownControllers = ConcurrentHashMap<String, (EntityInstance) -> (Controller)>()

    @TFunction.Init
    fun init() {
        KetherTypes.registerInternals(
                ScriptService.registry,
                ScriptService
        )
        val registry = ScriptService.registry
        // 系统逻辑
        registry.registerAction("log", ActionLog.parser())
        registry.registerAction("call", ActionCall.parser<QuestContext>())
        registry.registerAction("wait", ActionWait.parser())
        registry.registerAction("always", ActionAlways.parser())
        registry.registerAction("js", ActionJs.parser())
        registry.registerAction("set", ActionSet.parser())
        registry.registerAction("get", ActionGet.parser())
        registry.registerAction("run", ActionRun.parser())
        registry.registerAction("check", ActionCheck.parser())
        registry.registerAction("pause", ActionPause.parser())

        // 无参语法
        registry.registerAction("respawn", ActionRespawn.parser())
        registry.registerAction("destroy", ActionDestroy.parser())
        registry.registerAction("remove", ActionRemove.parser())
        registry.registerAction("delete", ActionDelete.parser())
        registry.registerAction("still", ActionMove.parser())
        registry.registerAction("sleeping", ActionSleeping.parser())

        // 有参语法
        registry.registerAction("use", ActionUse.parser())
        registry.registerAction("select", ActionSelect.parser())
        registry.registerAction("create", ActionCreate.parser())
        registry.registerAction("look", ActionLook.parser())
        registry.registerAction("move", ActionMove.parser())
        registry.registerAction("teleport", ActionTeleport.parser())
        registry.registerAction("tag", ActionTag.parser())
        registry.registerAction("meta", ActionMeta.parser())
        registry.registerAction("animation", ActionAnimation.parser())

        // 集合类型
        registry.registerAction("viewer", ActionLook.parser())
        registry.registerAction("passenger", ActionPassenger.parser())
        registry.registerAction("controller", ActionController.parser())

        // bukkit 相关
        registry.registerAction("event", ActionEvent.parser())
        registry.registerAction("listen", ActionListen.parser())
        registry.registerAction("command", ActionCommand.parser())
        registry.registerAction("continue", ActionContinue.parser())
        registry.registerAction("permission", ActionPermission.parser())
        registry.registerAction("placeholder", ActionPlaceholder.parser())

        // 已知监听器
        knownEvents["join"] = KnownEvent(PlayerJoinEvent::class)
                .field("player", { it.player.name })
                .field("message", { it.joinMessage }, { k, v -> k.joinMessage = v.toString() })

        knownEvents["quit"] = KnownEvent(PlayerQuitEvent::class)
                .field("player", { it.player.name })
                .field("message", { it.quitMessage }, { k, v -> k.quitMessage = v.toString() })

        // 已知控制器
        knownControllers["Move"] = { GeneralMove(it) }
        knownControllers["Gravity"] = { GeneralGravity(it) }
        knownControllers["SmoothLook"] = { GeneralSmoothLook(it) }
        knownControllers["LookAtPlayer"] = { ControllerLookAtPlayer(it) }
        knownControllers["RandomLookGround"] = { ControllerRandomLookaround(it) }
        knownControllers["RandomStrollLand"] = { ControllerRandomStrollLand(it) }
    }

    @TSchedule
    fun tick() {
        try {
            storage = LocalYamlStorage(ScriptService, Files.folder(Adyeshach.plugin.dataFolder, "npc/script data").toPath())
            storage!!.init()
        } catch (e: Exception) {
            println("[Adyeshach] Script data storage initialization failed")
            e.printStackTrace()
        }
        try {
            ScriptService.loadAll()
        } catch (e: Exception) {
            println("[Adyeshach] An error occurred while loading the script")
            e.printStackTrace()
        }
    }

    fun toEulerAngle(str: String): EulerAngle {
        val args = str.split(",")
        return EulerAngle(
                Coerce.toDouble(args.getOrElse(0) { "0" }),
                Coerce.toDouble(args.getOrElse(1) { "0" }),
                Coerce.toDouble(args.getOrElse(2) { "0" })
        )
    }

    fun toPosition(str: String): Position {
        val args = str.split(",")
        return Position(
                Coerce.toInteger(args.getOrElse(0) { "0" }),
                Coerce.toInteger(args.getOrElse(1) { "0" }),
                Coerce.toInteger(args.getOrElse(2) { "0" })
        )
    }

    fun toLocation(str: String): Location {
        val args = str.split(",")
        return Location(
                Bukkit.getWorld(args[0]),
                Coerce.toDouble(args.getOrElse(1) { "0" }),
                Coerce.toDouble(args.getOrElse(2) { "0" }),
                Coerce.toDouble(args.getOrElse(3) { "0" }),
                Coerce.toFloat(args.getOrElse(4) { "0" }),
                Coerce.toFloat(args.getOrElse(5) { "0" })
        )
    }

    fun getKnownEvent(name: String): KnownEvent<*>? {
        return knownEvents.entries.firstOrNull { it.key.equals(name, true) }?.value
    }

    fun getKnownController(name: String): ((EntityInstance) -> (Controller))? {
        return knownControllers.entries.firstOrNull { it.key.equals(name, true) }?.value
    }
}