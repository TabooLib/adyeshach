package ink.ptms.adyeshach.impl.entity.trait.impl

import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.impl.entity.trait.Trait
import ink.ptms.adyeshach.impl.util.Inputs.inputBook
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.kether.KetherShell
import taboolib.module.kether.runKether
import taboolib.platform.compat.replacePlaceholder
import java.awt.Color
import java.util.UUID
import java.util.concurrent.CompletableFuture

object TraitCommand : Trait() {

    val workers = HashMap<UUID, CommandWorker>()

    enum class Type {

        LEFT, RIGHT
    }

    @SubscribeEvent
    fun save(e: PlayerSwapHandItemsEvent) {
        if (e.player.uniqueId !in workers) return
        val worker = workers.remove(e.player.uniqueId)
        if (e.player.isSneaking) worker?.cancel() else worker?.save()
        e.isCancelled = true
    }

    @SubscribeEvent
    fun quit(e: PlayerQuitEvent) {
        workers.remove(e.player.uniqueId)
    }

    @SubscribeEvent
    private fun onRemove(e: AdyeshachEntityRemoveEvent) {
        workers.entries.removeIf { it.value.entity == e.entity }
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
        when (AdyeshachSettings.traitCommand) {
            "CHAT" -> workers.compute(player.uniqueId) { _, _ ->
                CommandWorker(player, entityInstance, future)
            }?.display()

            "BOOK" -> {
                language.sendLang(player, "trait-command")
                player.inputBook(data.getStringList(entityInstance.uniqueId)) {
                    entityInstance.setTraitCommands(it.map { it.replace("§0", "") })
                    future.complete(null)
                }
            }
        }
        return future
    }
}

class CommandWorker(val player: Player, val entity: EntityInstance, val complete: CompletableFuture<Void>) {

    val origin = entity.getTraitCommands()

    val modify = ArrayList<String>(origin)

    fun display() {
        val json = Components.empty()
        repeat(100) {
            json.newLine()
        }
        json.append(Components.text("绑定指令快速编辑").color(Color.GRAY))
            .append(Components.text("  |  按下 ").color(Color.GRAY))
            .append(Components.text("F").color(Color.GREEN))
            .append(Components.text(" 键保存").color(Color.GRAY))
            .newLine()
            .append(Components.text("按下 ").color(Color.GRAY))
            .append(Components.text("SHIFT + F").color(Color.RED))
            .append(Components.text(" 取消修改").color(Color.GRAY))
            .newLine()
        modify.forEachIndexed { index, s ->
            appendIndex(index, json)
            appendText(s, json)
            appendModify(index, s, json)
            appendMinus(index, json)
            json.newLine()
        }
        appendPlus(json)
        json.sendTo(adaptPlayer(player))
    }

    fun appendModify(index: Int, text: String, json: ComponentText) {
        val command = "adyeshach api se adyeshach api worker modify $index=$text"
        json.append(Components.text(" [").appendKeybind("✎").color(Color.ORANGE).clickRunCommand(command).hoverText("点击修改").append("]"))
    }

    fun appendMinus(index: Int, json: ComponentText) {
        val command = "adyeshach api se adyeshach api worker minus $index"
        json.append(Components.text(" [").appendKeybind("-").color(Color.RED).strikethrough().clickRunCommand(command).hoverText("点击删除").append("]"))
    }

    fun appendPlus(json: ComponentText) {
        val command = "adyeshach api se adyeshach api worker append"
        json.append(Components.text("[").appendKeybind("+").color(Color.ORANGE).clickRunCommand(command).hoverText("点击添加新指令").append("]"))
    }

    fun appendText(text: String, json: ComponentText) {
        json.append(text)
    }

    fun appendIndex(index: Int, json: ComponentText) {
        val i = Components.text("${index + 1}: ")
        json.append(i)
    }

    fun append(command: String): CommandWorker {
        modify.add(command)
        return this
    }

    fun cancel() {
        complete.complete(null)
    }

    fun save() {
        entity.setTraitCommands(modify)
        complete.complete(null)
    }

    fun take(index: Int): CommandWorker {
        modify.removeAt(index)
        return this
    }

    fun setIndex(index: Int, text: String) {
        modify[index] = text
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
