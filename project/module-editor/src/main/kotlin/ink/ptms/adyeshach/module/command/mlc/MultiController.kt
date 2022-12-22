package ink.ptms.adyeshach.module.command.mlc

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.util.safeDistance
import ink.ptms.adyeshach.module.command.getName
import ink.ptms.adyeshach.module.editor.clearScreen
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.platform.util.sendLang
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.command.mlc.MultiController
 *
 * @author 坏黑
 * @since 2022/12/17 23:59
 */
open class MultiController(val sender: CommandSender, val action: String, val entityList: List<EntityInstance>) {

    var id = ""
    var isNearby = false

    /** 输出列表 */
    fun print(): MultiController {
        return print(isNearby, id, true)
    }

    /** 输出列表 */
    fun print(isNearby: Boolean, id: String = "", update: Boolean = false): MultiController {
        this.isNearby = isNearby
        this.id = id
        sender.clearScreen()
        sender.sendLang("${if (isNearby) "command-find-near" else "command-find-more"}${if (update) "-update" else ""}", id)
        // 打印列表
        entityList.forEach {
            // 粒子效果
            if (sender is Player) {
                sender.spawnParticle(Particle.END_ROD, it.getLocation(), 150, 0.0, 8.0, 0.0, 0.0)
            }
            // 距离
            val distance = if (sender is Player) "${Coerce.format(it.getLocation().safeDistance(sender.location))}m" else "0m"
            // 提示信息中的固定参数
            // 0 —— 单位类型
            // 1 —— 单位名称
            // 2 —— 单位 UUID
            // 3 —— 距离
            val args = arrayOf(it.entityType, it.getName(), it.uniqueId, distance)
            // 提示信息
            if (it.isRemoved) {
                sender.sendLang("command-find-more-$action-update", *args)
            } else {
                sender.sendLang("command-find-more-$action", *args)
            }
        }
        return this
    }

    companion object {

        val controllerMap = ConcurrentHashMap<String, MultiController>()

        fun check(sender: CommandSender) {
            controllerMap[sender.name]?.print()
        }
    }
}