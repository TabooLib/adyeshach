package ink.ptms.adyeshach.module.command

import com.github.benmanes.caffeine.cache.Caffeine
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.util.safeDistance
import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.editor.clearScreen
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import java.util.concurrent.TimeUnit

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.command.EntityTracker
 *
 * @author 坏黑
 * @since 2022/12/17 23:59
 */
open class EntityTracker(val sender: CommandSender, val action: String, val entitySource: EntitySource) {

    var id = "null"
    var isNearby = false

    fun printNearby(): EntityTracker {
        return print(true)
    }

    fun printById(id: String): EntityTracker {
        return print(false, id)
    }

    fun print(): EntityTracker {
        return print(isNearby, id, true)
    }

    fun print(isNearby: Boolean, id: String? = null, update: Boolean = false): EntityTracker {
        this.id = id ?: "null"
        this.isNearby = isNearby
        sender.clearScreen()
        sender.sendLang("${if (isNearby) "command-find-near" else "command-find-more"}${if (update) "-update" else ""}", this.id)
        // 打印列表
        entitySource.elements.forEach {
            // 粒子效果
            if (sender is Player) {
                sender.spawnParticle(Particle.END_ROD, it.getLocation(), 150, 0.0, 8.0, 0.0, 0.0)
            }
            // 距离
            val distance = if (sender is Player) "${Coerce.format(it.getLocation().safeDistance(sender.location))}m" else "0m"
            // 固定参数
            val args = mutableListOf<Any>(it.entityType to "type", it.getName() to "name", it.uniqueId to "uuid", distance to "distance")
            // 附加参数
            entitySource.extraArgs(it).forEach { e -> args += e }
            // 提示信息
            if (entitySource.isUpdated(it)) {
                sender.sendLang("command-find-more-$action-update", *args.toTypedArray())
            } else {
                sender.sendLang("command-find-more-$action", *args.toTypedArray())
            }
        }
        return this
    }

    companion object {

        val trackerMap = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build<String, EntityTracker>()

        fun get(sender: CommandSender, action: String): EntityTracker? {
            val printer = trackerMap.getIfPresent(sender.name) ?: return null
            if (printer.action == action) {
                return printer
            }
            return null
        }

        fun check(sender: CommandSender, action: String, checkEntity: EntityInstance? = null) {
            val printer = trackerMap.getIfPresent(sender.name) ?: return
            if (printer.action == action && (checkEntity == null || checkEntity in printer.entitySource.elements)) {
                printer.print()
            }
        }
    }
}