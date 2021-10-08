package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.ai.ControllerGenerator
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
import taboolib.common.platform.function.getDataFolder
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import taboolib.library.kether.LocalizedException
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.Workspace
import java.io.File

@SkipTo(LifeCycle.LOAD)
object ScriptHandler {

    val workspace = Workspace(File(getDataFolder(), "npc/script"), ".ady", listOf("adyeshach"))

    @Awake(LifeCycle.LOAD)
    fun load() {
        AdyeshachAPI.registeredControllerGenerator["Move"] = ControllerGenerator(GeneralMove::class.java) { GeneralMove(it) }
        AdyeshachAPI.registeredControllerGenerator["Gravity"] = ControllerGenerator(GeneralGravity::class.java) { GeneralGravity(it) }
        AdyeshachAPI.registeredControllerGenerator["SmoothLook"] = ControllerGenerator(GeneralSmoothLook::class.java) { GeneralSmoothLook(it) }
        AdyeshachAPI.registeredControllerGenerator["LookAtPlayer"] = ControllerGenerator(ControllerLookAtPlayer::class.java) { ControllerLookAtPlayer(it) }
        AdyeshachAPI.registeredControllerGenerator["LookAtPlayerAlways"] = ControllerGenerator(ControllerLookAtPlayerAlways::class.java) { ControllerLookAtPlayerAlways(it) }
        AdyeshachAPI.registeredControllerGenerator["RandomLookGround"] = ControllerGenerator(ControllerRandomLookGround::class.java) { ControllerRandomLookGround(it) }
        AdyeshachAPI.registeredControllerGenerator["RandomStrollLand"] = ControllerGenerator(ControllerRandomStrollLand::class.java) { ControllerRandomStrollLand(it) }
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

    fun getControllerGenerator(name: String): ControllerGenerator? {
        return AdyeshachAPI.getControllerGenerator(name)
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