package ink.ptms.adyeshach.common.script

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
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.EulerAngle
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.SkipTo
import taboolib.common.platform.getDataFolder
import taboolib.common.util.Vector
import taboolib.common5.Coerce
import taboolib.library.kether.LocalizedException
import taboolib.module.kether.Kether
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.Workspace
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@SkipTo(LifeCycle.ENABLE)
object ScriptHandler {

    val workspace = Workspace(File(getDataFolder(), "npc/script"), ".ady", listOf("adyeshach"))
    val controllers = ConcurrentHashMap<String, KnownController>()

    @Awake(LifeCycle.ENABLE)
    fun load() {
        // 已知控制器
        controllers["Move"] = KnownController(GeneralMove::class) { GeneralMove(it) }
        controllers["Gravity"] = KnownController(GeneralGravity::class) { GeneralGravity(it) }
        controllers["SmoothLook"] = KnownController(GeneralSmoothLook::class) { GeneralSmoothLook(it) }
        controllers["LookAtPlayer"] = KnownController(ControllerLookAtPlayer::class) { ControllerLookAtPlayer(it) }
        controllers["LookAtPlayerAlways"] = KnownController(ControllerLookAtPlayerAlways::class) { ControllerLookAtPlayerAlways(it) }
        controllers["RandomLookGround"] = KnownController(ControllerRandomLookGround::class) { ControllerRandomLookGround(it) }
        controllers["RandomStrollLand"] = KnownController(ControllerRandomStrollLand::class) { ControllerRandomStrollLand(it) }
        // 监听器
        Kether.addEvent<AdyeshachEntityDamageEvent>("npc_damage")
        Kether.addEvent<AdyeshachEntityInteractEvent>("npc_interact")
    }

    fun toEulerAngle(str: String): EulerAngle {
        val args = str.split(",")
        return EulerAngle(
            Coerce.toDouble(args.getOrElse(0) { "0" }),
            Coerce.toDouble(args.getOrElse(1) { "0" }),
            Coerce.toDouble(args.getOrElse(2) { "0" })
        )
    }

    fun toVector(str: String): Vector {
        val args = str.split(",")
        return Vector(
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
        return controllers.entries.firstOrNull { it.key.equals(name, true) }?.value
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

    fun loadError(message: String): LocalizedException {
        return LocalizedException.of("load-error.custom", message)
    }
}