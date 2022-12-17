package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.util.safeDistance
import ink.ptms.adyeshach.module.editor.clearScreen
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.platform.util.sendLang
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.command.EntityGroup
 *
 * @author 坏黑
 * @since 2022/12/17 23:59
 */
class EntityGroup(val sender: CommandSender, val action: String, val entityList: List<EntityInstance>) {

    var id = ""
    var isNearby = false

    /** 输出列表 */
    fun print(): EntityGroup {
        return print(isNearby, id, true)
    }

    /** 输出列表 */
    fun print(isNearby: Boolean, id: String = "", update: Boolean = false): EntityGroup {
        this.isNearby = isNearby
        this.id = id
        sender.clearScreen()
        sender.sendLang("${if (isNearby) "command-find-near" else "command-find-more"}${if (update) "-update" else ""}", id)
        // 打印列表
        entityList.forEach {
            val distance = if (sender is Player) {
                // 连线
                sender.spawnParticle(Particle.END_ROD, it.getLocation(), 150, 0.0, 8.0, 0.0, 0.0)
                // 返回距离
                "${Coerce.format(it.getLocation().safeDistance(sender.location))}m"
            } else {
                "N/A"
            }
            if (it.isRemoved) {
                sender.sendLang("command-find-more-$action-update", it.entityType, it.getName(), it.uniqueId)
            } else {
                sender.sendLang("command-find-more-$action", it.entityType, it.getName(), it.uniqueId, distance)
            }
        }
        return this
    }

    companion object {

        val groupList = ConcurrentHashMap<String, EntityGroup>()

        fun check(sender: CommandSender) {
            groupList[sender.name]?.print()
        }
    }
}