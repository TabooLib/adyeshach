package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import ink.ptms.adyeshach.core.util.safeDistance
import ink.ptms.adyeshach.core.util.sendLang
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.command.CommandBuilder
import taboolib.common.platform.function.allWorlds
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.xseries.XMaterial
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 格式化数字（保留两位小数）
 */
fun format(value: Double): String {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).toString()
}

fun format(value: Float): String {
    return BigDecimal.valueOf(value.toDouble()).setScale(2, RoundingMode.HALF_UP).toString()
}

/**
 * 取所有世界名称
 */
fun worlds(): List<String> {
    return allWorlds().toMutableList().also { it += "~" }
}

/**
 * 获取单位名称，如果是玩家则获取玩家名称
 */
fun EntityInstance.getName(): String {
    return if (this is AdyHuman) getName() else getDisplayName()
}

fun CommandBuilder.CommandComponentDynamic.suggestEntityList() {
    suggestion<CommandSender>(uncheck = true) { sender, _ ->
        Command.finder.getEntities(if (sender is Player) sender else null) { !it.isDerived() }.map { it.id }
    }
}

/**
 * 就近复选操作
 */
inline fun <reified T : EntitySource> multiControl(sender: Player, action: String, singleAction: (EntityInstance) -> Unit = {}) {
    val npcList = Command.finder.getEntities(sender) { it.getLocation().safeDistance(sender.location) < 16 && !it.isDerived() }
    if (npcList.isEmpty()) {
        sender.sendLang("command-find-empty")
    } else if (npcList.size == 1) {
        singleAction(npcList.first())
    } else {
        EntityTracker.trackerMap.put(sender.name, EntityTracker(sender, action, T::class.java.invokeConstructor(npcList)).printNearby())
    }
}

/**
 * 定向复选操作
 */
inline fun <reified T : EntitySource> multiControl(
    sender: CommandSender,
    id: String,
    action: String,
    unified: Boolean = true,
    singleAction: (EntityInstance) -> Unit = {}
) {
    val npcList = Command.finder.getEntitiesFromIdOrUniqueId(id, if (sender is Player) sender else null).filter { !it.isDerived() }
    when {
        // 空列表
        npcList.isEmpty() -> {
            sender.sendLang("command-find-empty")
        }
        // 多实体
        npcList.size > 1 -> {
            EntityTracker.trackerMap.put(sender.name, EntityTracker(sender, action, T::class.java.invokeConstructor(npcList)).printBy(id))
            // 统一操作
            if (unified) {
                sender.sendLang("command-find-more-$action-all", id)
            }
        }
        // 单实体
        else -> {
            singleAction(npcList.first())
        }
    }
}

fun EntityTypes.toEgg(): ItemStack {
    return XMaterial.matchXMaterial("${name}_SPAWN_EGG").orElse(XMaterial.SKELETON_SPAWN_EGG).parseItem() ?: ItemStack(Material.STONE)
}