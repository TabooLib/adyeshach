package ink.ptms.adyeshach.impl.entity.trait.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.impl.entity.trait.Trait
import ink.ptms.adyeshach.impl.util.Inputs.inputBook
import org.bukkit.entity.Player
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.module.kether.KetherShell
import taboolib.module.kether.runKether
import taboolib.platform.compat.replacePlaceholder
import java.util.concurrent.CompletableFuture

object TraitCommand : Trait() {

    enum class Type {

        LEFT, RIGHT
    }

    @SubscribeEvent
    private fun onRemove(e: AdyeshachEntityRemoveEvent) {
        data[e.entity.uniqueId] = null
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onInteract(e: AdyeshachEntityDamageEvent) {
        runCommand(e.player, e.entity, Type.LEFT)
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onInteract(e: AdyeshachEntityInteractEvent) {
        if (e.isMainHand) {
            runCommand(e.player, e.entity, Type.RIGHT)
        }
    }

    fun runCommand(player: Player, entity: EntityInstance, type: Type) {
        // 格式化信息
        fun String.format(): String {
            return trim().replace("@player", player.name).replacePlaceholder(player)
        }
        // 获取命令列表
        data.getStringList(entity.uniqueId).forEach {
            var line = it
            for (t in Type.values()) {
                // 匹配类型
                if (line.endsWith("~${t.name.lowercase()}")) {
                    // 类型不符
                    if (t != type) {
                        return@forEach
                    }
                    line = line.substringBeforeLast("~${t.name.lowercase()}")
                    break
                }
            }
            when {
                // 管理员权限执行
                line.startsWith("op:") -> {
                    val isOp = player.isOp
                    player.isOp = true
                    try {
                        adaptPlayer(player).performCommand(line.substringAfter("op:").format())
                    } catch (ex: Throwable) {
                        ex.printStackTrace()
                    }
                    player.isOp = isOp
                }
                // 服务器控制台执行
                line.startsWith("server:") -> {
                    console().performCommand(line.substringAfter("server:").format())
                }
                // 服务器控制台执行
                line.startsWith("console:") -> {
                    console().performCommand(line.substringAfter("console:").format())
                }
                // Kether 脚本执行
                line.startsWith("kether:") -> {
                    runKether {
                        KetherShell.eval(line.substringAfter("kether:").trim(), namespace = listOf("adyeshach"), sender = adaptPlayer(player)) {
                            set("@entities", entity)
                            set("@manager", entity.manager)
                        }
                    }
                }
                // 普通命令执行
                else -> {
                    adaptPlayer(player).performCommand(line.replace("@player", player.name).replacePlaceholder(player))
                }
            }
        }
    }

    override fun id(): String {
        return "command"
    }

    override fun edit(player: Player, entityInstance: EntityInstance): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        language.sendLang(player, "trait-command")
        player.inputBook(data.getStringList(entityInstance.uniqueId)) {
            entityInstance.setTraitCommands(it.map { it.replace("§0", "") })
            future.complete(null)
        }
        return future
    }
}

fun EntityInstance.setTraitCommands(commands: List<String>?) {
    if (commands == null || commands.all { line -> line.isBlank() }) {
        TraitCommand.data[uniqueId] = null
    } else {
        TraitCommand.data[uniqueId] = commands
    }
}

fun EntityInstance.getTraitCommands(): List<String> {
    return TraitCommand.data.getStringList(uniqueId)
}