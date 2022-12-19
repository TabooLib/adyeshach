package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import ink.ptms.adyeshach.core.util.safeDistance
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBuilder
import taboolib.platform.util.sendLang

/**
 * 获取单位名称，如果是玩家则获取玩家名称
 */
fun EntityInstance.getName(): String {
    return if (this is AdyHuman) getName() else getDisplayName()
}

fun CommandBuilder.CommandComponentDynamic.suggestEntityList() {
    suggestion<CommandSender>(uncheck = true) { sender, _ -> Command.finder.getEntities(if (sender is Player) sender else null).map { it.id } }
}

/**
 * 就近复选操作
 */
fun multi(sender: Player, action: String) {
    val npcList = Command.finder.getEntities(sender) { it.getLocation().safeDistance(sender.location) < 16 }
    if (npcList.isEmpty()) {
        sender.sendLang("command-find-empty")
    } else {
        EntityGroup.groupList[sender.name] = EntityGroup(sender, action, npcList).print(isNearby = true)
    }
}

/**
 * 定向复选操作
 */
fun multi(sender: CommandSender, id: String, action: String, all: Boolean = true, singleAction: (EntityInstance) -> Unit = {}) {
    val npcList = Command.finder.getEntitiesFromIdOrUniqueId(id, if (sender is Player) sender else null)
    when {
        // 空列表
        npcList.isEmpty() -> {
            sender.sendLang("command-find-empty")
        }
        // 多实体
        npcList.size > 1 -> {
            EntityGroup.groupList[sender.name] = EntityGroup(sender, action, npcList).print(isNearby = false, id)
            // 统一操作
            if (all) {
                sender.sendLang("command-find-more-$action-all", id)
            }
        }
        // 单实体
        else -> {
            singleAction(npcList.first())
        }
    }
}