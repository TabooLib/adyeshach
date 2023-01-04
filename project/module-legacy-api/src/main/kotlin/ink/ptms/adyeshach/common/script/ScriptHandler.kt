package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.ai.ControllerGenerator
import ink.ptms.adyeshach.impl.DefaultScriptManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import taboolib.library.kether.LocalizedException

@Deprecated("Outdated but usable")
object ScriptHandler {

    val workspace = DefaultScriptManager.workspace

    fun load() {
    }

    fun loadCustomController() {
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

//    fun ScriptContext.getManager(): Manager? {
//        return rootFrame().variables().get<Manager?>("@manager").orElse(null)
//    }
//
//    fun ScriptContext.setManager(manager: Manager?) {
//        rootFrame().variables().set("@manager", manager)
//    }
//
//    fun ScriptContext.getEntities(): List<EntityInstance?>? {
//        return rootFrame().variables().get<List<EntityInstance?>?>("@entities").orElse(null)
//    }
//
//    fun ScriptContext.setEntities(entities: List<EntityInstance?>?) {
//        rootFrame().variables().set("@entities", entities)
//    }
//
//    fun ScriptContext.isEntitySelected(): Boolean {
//        return getEntities() != null && getEntities()!!.filterNotNull().isNotEmpty()
//    }

    fun loadError(message: String): LocalizedException {
        return LocalizedException.of("load-error.custom", message)
    }
}