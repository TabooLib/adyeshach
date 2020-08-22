package ink.ptms.adyeshach.common.editor

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.element.VillagerData
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.book.BookFormatter
import io.izzel.taboolib.util.chat.ComponentSerializer
import io.izzel.taboolib.util.lite.Signs
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions
import kotlin.reflect.KClass

object Editors {

    val TEXT = EntityMetaable.MetaEditor()
            .onModify { player, entity, meta ->
                Signs.fakeSign(player, arrayOf("${entity.getMetadata<Any>(meta.key)}", "", "请在第一行输入内容")) {
                    if (it[0].isNotEmpty()) {
                        entity.setMetadata(meta.key, it.joinToString("").replace("&", "§"))
                    }
                    Editor.open(player, entity)
                }
            }
            .onDisplay { _, entity, meta ->
                val display = entity.getMetadata<Any>(meta.key).toString()
                if (display.isEmpty()) "§7_" else Editor.toSimple(display)
            }

    val COLOR = EntityMetaable.MetaEditor()
            .onModify { player, entity, meta ->
                Signs.fakeSign(player, arrayOf(meta.editor!!.onDisplay!!.invoke(player, entity, meta), "", "请在第一行输入内容")) {
                    if (it[0].isNotEmpty()) {
                        val v = it[0].split("-").map { a -> NumberConversions.toInt(a) }
                        entity.setMetadata(meta.key, Color.fromRGB(v.getOrElse(0) { 0 }, v.getOrElse(1) { 0 }, v.getOrElse(2) { 0 }).asRGB())
                    }
                    Editor.open(player, entity)
                }
            }
            .onDisplay { _, entity, meta ->
                val color = Color.fromRGB(entity.getMetadata(meta.key))
                if (Editor.version >= 11600) {
                    "${io.izzel.taboolib.util.chat.ChatColor.of(java.awt.Color(color.red, color.green, color.blue))}${color.red}-${color.green}-${color.blue}"
                } else {
                    "${color.red}-${color.green}-${color.blue}"
                }
            }

    val ENUMS = HashMap<String, EntityMetaable.MetaEditor>()

    fun enums(enum: KClass<*>, command: (Player, EntityInstance, EntityMetaable.Meta, Any) -> (String)): EntityMetaable.MetaEditor {
        return ENUMS.computeIfAbsent(enum.java.name) {
            EntityMetaable.MetaEditor()
                    .onModify { player, entity, meta ->
                        val book = BookFormatter.writtenBook()
                        var page = TellrawJson.create()
                        var i = 0
                        enum.java.enumConstants.forEach {
                            page.append("  $it")
                                    .clickCommand(command.invoke(player, entity, meta, it))
                                    .hoverText("§nClick To Select")
                                    .newLine()
                            if (++i == 12) {
                                i = 0
                                book.addPages(ComponentSerializer.parse(page.toRawMessage(player)))
                                page = TellrawJson.create()
                            }
                        }
                        if (i > 0) {
                            book.addPages(ComponentSerializer.parse(page.toRawMessage(player)))
                        }
                        book.open(player)
                    }
        }
    }
}