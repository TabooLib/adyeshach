package ink.ptms.adyeshach.common.editor

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.book.BookFormatter
import io.izzel.taboolib.util.chat.ComponentSerializer
import io.izzel.taboolib.util.lite.Numbers
import io.izzel.taboolib.util.lite.Signs
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions
import kotlin.reflect.KClass

object Editor {

    val editMethod = HashMap<KClass<*>, EntityMetaable.MetaEditor>()

    init {
        editMethod[Int::class] = EntityMetaable.MetaEditor()
                .onModify { player, entity, meta ->
                    Signs.fakeSign(player, arrayOf("${entity.getMetadata<Int>(meta.key)}", "", "请在第一行输入数字")) {
                        entity.setMetadata(meta.key, NumberConversions.toInt(it[0]))
                        open(player, entity)
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
        book.open(player)
    }

    fun Boolean.toDisplay(): String {
        return if (this) "Yes" else "No"
    }
}