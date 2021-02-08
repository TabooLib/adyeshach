package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerLookAtPlayer
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerLookAtPlayerAlways
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerRandomLookGround
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerRandomStrollLand
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.script.action.*
import io.izzel.taboolib.kotlin.kether.Kether
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.Workspace
import io.izzel.taboolib.kotlin.kether.common.api.QuestActionParser
import io.izzel.taboolib.kotlin.kether.common.util.LocalizedException
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.nms.impl.Position
import io.izzel.taboolib.util.Coerce
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.EulerAngle
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object ScriptHandler {

    val workspace = Workspace(File(Adyeshach.plugin.dataFolder, "npc/script"), ".ady", listOf("adyeshach"))
    val knownControllers = ConcurrentHashMap<String, KnownController>()

    fun addAction(id: String, parser: QuestActionParser) {
        Kether.addAction(id, parser, "adyeshach")
    }

    @TFunction.Init
    fun init() {
        // 无参语法
        addAction("respawn", ActionRespawn.parser())
        addAction("destroy", ActionDestroy.parser())
        addAction("remove", ActionRemove.parser())
        addAction("delete", ActionDelete.parser())
        addAction("still", ActionMove.parser())
        addAction("sleeping", ActionSleeping.parser())
        // 有参语法
        addAction("use", ActionUse.parser())
        addAction("select", ActionSelect.parser())
        addAction("create", ActionCreate.parser())
        addAction("look", ActionLook.parser())
        addAction("move", ActionMove.parser())
        addAction("teleport", ActionTeleport.parser())
        addAction("tag", ActionTag.parser())
        addAction("meta", ActionMeta.parser())
        addAction("animation", ActionAnimation.parser())
        // 集合类型
        addAction("viewer", ActionViewer.parser())
        addAction("passenger", ActionPassenger.parser())
        addAction("controller", ActionController.parser())
        // 监听器
        Kether.addEventOperator("npc_damage", AdyeshachEntityDamageEvent::class) {
            unit("player") {
                reader = { it.player.name }
            }
            unit("id") {
                reader = { it.entity.id }
            }
            unit("uniqueId") {
                reader = { it.entity.uniqueId }
            }
            unit("cancelled") {
                reader = { it.isCancelled }
                writer = { k, v -> k.isCancelled = Coerce.toBoolean(v) }
            }
        }
        Kether.addEventOperator("npc_interact", AdyeshachEntityInteractEvent::class) {
            unit("player") {
                reader = { it.player.name }
            }
            unit("id") {
                reader = { it.entity.id }
            }
            unit("uniqueId") {
                reader = { it.entity.uniqueId }
            }
            unit("action") {
                reader = { if (it.isMainHand) "HAND" else "OFF_HAND" }
            }
            unit("cancelled") {
                reader = { it.isCancelled }
                writer = { k, v -> k.isCancelled = Coerce.toBoolean(v) }
            }
        }
        // 已知控制器
        knownControllers["Move"] = KnownController(GeneralMove::class) { GeneralMove(it) }
        knownControllers["Gravity"] = KnownController(GeneralGravity::class) { GeneralGravity(it) }
        knownControllers["SmoothLook"] = KnownController(GeneralSmoothLook::class) { GeneralSmoothLook(it) }
        knownControllers["LookAtPlayer"] = KnownController(ControllerLookAtPlayer::class) { ControllerLookAtPlayer(it) }
        knownControllers["LookAtPlayerAlways"] = KnownController(ControllerLookAtPlayerAlways::class) { ControllerLookAtPlayerAlways(it) }
        knownControllers["RandomLookGround"] = KnownController(ControllerRandomLookGround::class) { ControllerRandomLookGround(it) }
        knownControllers["RandomStrollLand"] = KnownController(ControllerRandomStrollLand::class) { ControllerRandomStrollLand(it) }
    }

    @TSchedule
    fun tick() {
        try {
            workspace.loadAll()
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

    fun getKnownController(name: String): KnownController? {
        return knownControllers.entries.firstOrNull { it.key.equals(name, true) }?.value
    }

    fun ScriptContext.getManager(): Manager? {
        return rootFrame().variables().get<Manager?>("@manager").orElse(null)
    }

    fun ScriptContext.setManager(manager: Manager?) {
        rootFrame().variables().set("@manager", manager)
    }

    fun ScriptContext.getEntities(): List<EntityInstance?>? {
        return rootFrame().variables().get<List<EntityInstance?>?>("@entities").orElse(null)
    }

    fun ScriptContext.setEntities(entities: List<EntityInstance?>?) {
        rootFrame().variables().set("@entities", entities)
    }

    fun ScriptContext.entitySelected(): Boolean {
        return getEntities() != null && getEntities()!!.filterNotNull().isNotEmpty()
    }

    fun loadError(message: String) = LocalizedException.of("load-error.custom", message)
}