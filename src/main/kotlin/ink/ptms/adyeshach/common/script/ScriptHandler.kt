package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.ControllerGenerator
import ink.ptms.adyeshach.common.entity.ai.ControllerKether
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
import org.bukkit.util.Vector
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.SkipTo
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.kether.LocalizedException
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.mapSection
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.Workspace
import taboolib.module.lang.sendLang
import java.io.File

@SkipTo(LifeCycle.LOAD)
object ScriptHandler {

    val workspace = Workspace(File(getDataFolder(), "npc/script"), ".ady", listOf("adyeshach"))

    @Awake(LifeCycle.LOAD)
    fun load() {
        val map = AdyeshachAPI.registeredControllerGenerator
        map["Move"] = ControllerGenerator(GeneralMove::class.java) { GeneralMove(it) }
        map["Gravity"] = ControllerGenerator(GeneralGravity::class.java) { GeneralGravity(it) }
        map["SmoothLook"] = ControllerGenerator(GeneralSmoothLook::class.java) { GeneralSmoothLook(it) }
        map["LookAtPlayer"] = ControllerGenerator(ControllerLookAtPlayer::class.java) { ControllerLookAtPlayer(it) }
        map["LookAtPlayerAlways"] = ControllerGenerator(ControllerLookAtPlayerAlways::class.java) { ControllerLookAtPlayerAlways(it) }
        map["RandomLookGround"] = ControllerGenerator(ControllerRandomLookGround::class.java) { ControllerRandomLookGround(it) }
        map["RandomStrollLand"] = ControllerGenerator(ControllerRandomStrollLand::class.java) { ControllerRandomStrollLand(it) }
        // 加载自定义脚本
        loadCustomController()
    }

    fun loadCustomController() {
        var i = 0
        val map = AdyeshachAPI.registeredControllerGenerator
        // 删除旧的
        map.keys.filter { it.startsWith("inner:") }.forEach { map.remove(it) }
        // 加载新的
        File(getDataFolder(), "npc/controller").walk().filter { it.extension == "yml" }.forEach {
            // 从文件中读取配置
            Configuration.loadFromFile(it).mapSection { section ->
                map["inner:${section.name}"] = ControllerGenerator(ControllerKether::class.java) { e -> ControllerKether(e, section) }
                i++
            }
        }
        if (i > 0) {
            info("Loaded $i custom controller.")
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