package ink.ptms.adyeshach.internal.command

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.internal.mirror.Mirror
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.book.BookFormatter
import io.izzel.taboolib.util.book.builder.PageBuilder
import io.izzel.taboolib.util.chat.ComponentSerializer
import io.izzel.taboolib.util.lite.Numbers
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-05 0:05
 */
@BaseCommand(name = "adyeshachtest", aliases = ["atest"], permission = "adyeshach.command")
class CommandTest : BaseMainCommand(), Helper {

    @SubCommand(description = "verify the entity type.")
    var verify: BaseSubCommand = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            EntityTypes.values().forEach {
                try {
                    NMS.INSTANCE.getEntityTypeNMS(it)
                } catch (t: Throwable) {
                    sender.info("Error: ${it}")
                }
            }
            sender.info("Done.")
        }
    }

    @SubCommand(description = "print performance monitoring.", type = CommandType.PLAYER)
    var mirror: BaseSubCommand = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            sender.info("Creating...")
            val bookBuilder = BookFormatter.writtenBook()
            bookBuilder.addPages(PageBuilder()
                    .add("").newLine()
                    .add("").newLine()
                    .add("        §l§nAdyeshach").newLine()
                    .add("").newLine()
                    .add("   Performance Monitoring").newLine()
                    .build())
            Mirror.dataMap.keys.toList().sortedByDescending { Mirror.get(it).timeTotal }.forEach { k ->
                val v = Mirror.get(k)
                val name = k.substring(k.indexOf(":") + 1)
                bookBuilder.addPages(ComponentSerializer.parse(TellrawJson.create().newLine()
                        .append("  §1§l§n${k.split(":")[0]}").newLine()
                        .append("  §1" + toSimple(name)).hoverText(name).newLine()
                        .append("").newLine()
                        .append("  Total §7${v.times} times").newLine()
                        .append("  Total §7${v.timeTotal} ms").newLine()
                        .append("  Average §7${Numbers.format(v.timeLatest)} ms ").append("§4(?)").hoverText("§8Details:\n§fLowest §7${v.lowest} ms\n§fHighest §7${v.highest} ms").newLine()
                        .toRawMessage(sender as Player)))
            }
            sender.info("Created.")
            BookFormatter.forceOpen(sender as Player, bookBuilder.build())
        }
    }

    fun toSimple(source: String): String? {
        return if (source.length > 20) source.substring(0, source.length - (source.length - 10)) + "..." + source.substring(source.length - 7) else source
    }
}