package ink.ptms.adyeshach.impl.entity.controller

import com.google.gson.JsonObject
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.controller.Controller
import ink.ptms.adyeshach.core.entity.controller.ControllerGenerator
import ink.ptms.adyeshach.core.entity.controller.CustomSerializable
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.util.asList
import taboolib.common.util.orNull
import taboolib.common.util.random
import taboolib.common.util.resettableLazy
import taboolib.common5.Baffle
import taboolib.common5.cbool
import taboolib.common5.util.parseMillis
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.mapSection
import taboolib.module.kether.*
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.controller.ControllerKether
 *
 * @author 坏黑
 * @since 2023/1/3 22:23
 */
class KetherController(entity: EntityInstance, val root: ConfigurationSection) : Controller(entity), CustomSerializable {

    val id = root.name

    /** 概率 **/
    val chance = root.getDouble("chance", 1.0)

    /** 冷却 **/
    val cooldown = if (root.contains("cooldown")) Baffle.of(root.getString("cooldown")!!.parseMillis(), TimeUnit.MILLISECONDS) else null

    /** 是否异步 **/
    val async = root.getBoolean("async", false)

    /** 条件 **/
    val condition = root.getString("if")

    /** 动作 **/
    val action = arrayListOf<String>()

    /** 持久化变量 **/
    val variables = hashMapOf<String, Any?>()

    /** 是否被取消 **/
    var cancel = false

    init {
        action += root["then"]?.asList()?.toMutableList() ?: arrayListOf()
        action += "flush"
    }

    override fun isAsync(): Boolean {
        return async
    }

    override fun id(): String {
        return id
    }

    override fun shouldExecute(): Boolean {
        if (cancel || !random(chance)) {
            return false
        }
        if (cooldown != null && !cooldown.hasNext()) {
            return false
        }
        if (condition != null) {
            return runKether { runKether(listOf(condition), entity?.getFirstViewPlayer()).getNow(false).cbool }!!
        }
        return true
    }

    override fun start() {
        if (runKether { runKether(action, entity?.getFirstViewPlayer()) } == null) {
            cancel = true
        }
    }

    override fun serialize(): JsonObject {
        return JsonObject().also { it.addProperty("id", id) }
    }

    private fun EntityInstance.getFirstViewPlayer(): Player? {
        return viewPlayers.getViewPlayers().firstOrNull()
    }

    private fun runKether(script: List<String>, sender: Player?): CompletableFuture<Any?> {
        val vars = hashMapOf("@entities" to listOf(entity), "@manager" to entity?.manager, "@controller" to this)
        vars.putAll(variables)
        return KetherShell.eval(
            script,
            ScriptOptions.builder()
                .namespace(listOf("adyeshach", "adyeshach-inner", "chemdah"))
                .sender(sender ?: console())
                .vars(vars)
                .build()
        )
    }

    override fun toString(): String {
        return id.uppercase()
    }

    companion object {

        /** 加载自定义控制器 */
        private val customControllerLoader by resettableLazy {
            var i = 0
            val registry = Adyeshach.api().getEntityControllerRegistry()
            val map = registry.getControllerGenerator()
            // 删除旧的
            map.keys.filter { it.startsWith("inner:") }.forEach { registry.unregisterControllerGenerator(it) }
            // 加载新的
            File(getDataFolder(), "npc/controller").walk().filter { it.extension == "yml" }.forEach {
                // 从文件中读取配置
                Configuration.loadFromFile(it).mapSection { section ->
                    registry.registerControllerGenerator("inner:${section.name}", KetherControllerGenerator(section))
                    i++
                }
            }
            if (i > 0) {
                info("Loaded $i custom controller(s).")
            }
        }

        @Awake(LifeCycle.ENABLE)
        fun init() {
            customControllerLoader
        }

        @JvmStatic
        fun deserialize(data: JsonObject): ControllerGenerator {
            val id = data["id"].asString
            return Adyeshach.api().getEntityControllerRegistry().getControllerGenerator("inner:$id") ?: error("CustomController \"$id\" not found")
        }

        @KetherParser(["flush"], namespace = "adyeshach-inner")
        fun parser() = scriptParser {
            actionNow {
                val controller = script().rootFrame().variables().get<KetherController?>("@controller")?.orNull()
                if (controller != null) {
                    controller.variables.clear()
                    controller.variables.putAll(deepVars().filterKeys { it.startsWith("__") && it.endsWith("__") })
                }
            }
        }
    }
}