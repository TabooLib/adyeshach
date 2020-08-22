package ink.ptms.adyeshach.common.editor

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import io.izzel.taboolib.Version
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.book.BookFormatter
import io.izzel.taboolib.util.chat.ComponentSerializer
import io.izzel.taboolib.util.chat.TextComponent
import io.izzel.taboolib.util.lite.Numbers
import io.izzel.taboolib.util.lite.Signs
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object Editor {

    val version = Version.getCurrentVersionInt()
    val editMethod = HashMap<KClass<*>, EntityMetaable.MetaEditor>()

    init {
        editMethod[Boolean::class] = EntityMetaable.MetaEditor()
                .onModify { player, entity, meta ->
                    entity.setMetadata(meta.key, !entity.getMetadata<Boolean>(meta.key))
                    open(player, entity)
                }
                .onDisplay { _, entity, meta ->
                    entity.getMetadata<Boolean>(meta.key).toDisplay()
                }
        editMethod[Int::class] = Editors.TEXT
        editMethod[Float::class] = Editors.TEXT
        editMethod[Double::class] = Editors.TEXT
        editMethod[String::class] = Editors.TEXT
        editMethod[TextComponent::class] = Editors.TEXT
    }

    fun getEditor(meta: EntityMetaable.Meta): EntityMetaable.MetaEditor? {
        return when {
            meta.editor != null -> {
                meta.editor
            }
            meta is EntityMetaable.MetaNatural<*> -> {
                editMethod[meta.def.javaClass.kotlin]
            }
            else -> {
                editMethod[Boolean::class]
            }
        }
    }

    fun open(player: Player, entity: EntityInstance) {
        val book = BookFormatter.writtenBook()
        book.addPages(ComponentSerializer.parse(TellrawJson.create()
                .append("  §1§l§n${entity.entityType.bukkitType}").newLine()
                .append("  §1${entity.id} ${if (entity.isTemporary()) "§7(Temporary)" else ""}").newLine()
                .append("").newLine()
                .append("  Type §7${if (entity.isPublic()) "PUBLIC" else "PRIVATE"}").newLine()
                .append("  Viewers §7${entity.viewPlayers.viewers.size} ").append("§c(?)").hoverText(entity.viewPlayers.viewers.joinToString("\n")).newLine()
                .append("  Pathfinder §7${entity.pathfinder.size} ").append("§c(?)").hoverText(entity.pathfinder.joinToString("\n") { it.javaClass.name }).newLine()
                .append("").newLine()
                .append("   §7§oX ${entity.position.x}").newLine()
                .append("   §7§oY ${entity.position.y}").newLine()
                .append("   §7§oZ ${entity.position.z}").newLine()
                .append("   §7§oYaw ${entity.position.yaw}").newLine()
                .append("   §7§oPitch ${entity.position.pitch}").newLine()
                .toRawMessage(player)))
        var page = TellrawJson.create()
        var i = 0
        entity.listMetadata().sortedBy { it.key }.forEach {
            val editor = getEditor(it)
            if (editor != null) {
                page.append("  §n${it.key.toDisplay()}").hoverText("Index ${it.index}").newLine()
                try {
                    page.append("   §c✘")
                            .clickCommand("/adyeshachapi edit reset ${entity.uniqueId} ${it.key}")
                            .hoverText("§nClick To Reset")
                    page.append(" §7${if (editor.onDisplay != null) editor.onDisplay!!.invoke(player, entity, it) else entity.getMetadata<Any>(it.key)}")
                            .clickCommand("/adyeshachapi edit meta ${entity.uniqueId} ${it.key}")
                            .hoverText("§nClick To Edit")
                            .newLine()
                } catch (t: Throwable) {
                    page.append("   §c§o<ERROR>").hoverText(t.message).newLine()
                }
                if (++i == 6) {
                    i = 0
                    book.addPages(ComponentSerializer.parse(page.toRawMessage(player)))
                    page = TellrawJson.create()
                }
            }
        }
        if (i > 0) {
            book.addPages(ComponentSerializer.parse(page.toRawMessage(player)))
        }
        book.open(player)
    }

    fun Boolean.toDisplay(): String {
        return if (this) "§aTrue" else "§cFalse"
    }

    fun String.toDisplay(): String {
        val builder = StringBuilder()
        toCharArray().forEachIndexed { index, c ->
            when {
                index == 0 -> builder.append(c.toUpperCase())
                c.isUpperCase() -> builder.append(" $c")
                else -> builder.append(c)
            }
        }
        return builder.toString()
    }

    fun toSimple(source: String): String {
        return if (source.length > 20) source.substring(0, source.length - (source.length - 10)) + "..." + source.substring(source.length - 7) else source
    }
}