package ink.ptms.adyeshach.common.entity.ai

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.apache.commons.logging.LogFactory
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.asList
import taboolib.common.util.orNull
import taboolib.common.util.random
import taboolib.common5.Baffle
import taboolib.common5.cbool
import taboolib.common5.util.parseMillis
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.ai.ControllerKether
 *
 * @author 坏黑
 * @since 2022/12/11 00:50
 */
class ControllerKether(entity: EntityInstance, val root: ConfigurationSection) : Controller(entity) {

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

    override fun onTick() {
        if (runKether { runKether(action, entity?.getFirstViewPlayer()) } == null) {
            cancel = true
        }
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
                .sender(if (sender != null) adaptPlayer(sender) else null)
                .vars(vars)
                .build()
        )
    }

    companion object {

        @KetherParser(["flush"], namespace = "adyeshach-inner")
        fun parser() = scriptParser {
            actionNow {
                val controller = script().rootFrame().variables().get<ControllerKether?>("@controller")?.orNull()
                if (controller != null) {
                    controller.variables.clear()
                    controller.variables.putAll(deepVars().filterKeys { it.startsWith("__") && it.endsWith("__") })
                }
            }
        }
    }
}