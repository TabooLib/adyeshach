package ink.ptms.adyeshach.common.editor

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.bukkit.data.PositionNull
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import ink.ptms.adyeshach.internal.listener.ListenerArmorStand
import io.izzel.taboolib.Version
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.module.nms.impl.Position
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.KV
import io.izzel.taboolib.util.book.BookFormatter
import io.izzel.taboolib.util.chat.ComponentSerializer
import io.izzel.taboolib.util.chat.TextComponent
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.lite.Materials
import io.izzel.taboolib.util.lite.Numbers
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object Editor {

    val version = Version.getCurrentVersionInt()
    val editMethod = HashMap<KClass<*>, EntityMetaable.MetaEditor>()

    @PlayerContainer
    val editArmorStand = ConcurrentHashMap<String, KV<AdyArmorStand, ListenerArmorStand.Angle?>>()

    init {
        editMethod[Boolean::class] = EntityMetaable.MetaEditor()
                .modify { player, entity, meta ->
                    entity.setMetadata(meta.key, !entity.getMetadata<Boolean>(meta.key))
                    open(player, entity)
                }
                .display { _, entity, meta ->
                    entity.getMetadata<Boolean>(meta.key).toDisplay()
                }
        editMethod[Position::class] = EntityMetaable.MetaEditor()
                .modify { player, entity, meta ->
                    entity.setMetadata(meta.key, Position(player.location.blockX, player.location.blockY, player.location.blockZ))
                    open(player, entity)
                }
                .display { _, entity, meta ->
                    entity.getMetadata<Position>(meta.key).run {
                        if (this is PositionNull) "_" else "$x $y $z"
                    }
                }
        editMethod[EulerAngle::class] = EntityMetaable.MetaEditor()
                .modify { player, entity, meta ->
                    editArmorStand[player.name] = KV(entity as AdyArmorStand, null)
                    Items.takeItem(player.inventory, { Items.hasLore(it, "Adyeshach Tool") }, 99)
                    player.inventory.addItem(ItemBuilder(Materials.REDSTONE_TORCH.parseItem()).name("&7Angle: &fNONE").lore("&8Adyeshach Tool").shiny().colored().build())
                    player.sendMessage("§c[Adyeshach] §7Use the Angle Tool (REDSTONE_TORCH) to edit the ArmorStand NPC.")
                    player.closeInventory()
                }
                .display { player, entity, meta ->
                    entity.getMetadata<EulerAngle>(meta.key).run {
                        "${Numbers.format(x)} ${Numbers.format(y)} ${Numbers.format(z)}"
                    }
                }
        editMethod[Int::class] = Editors.TEXT
        editMethod[Byte::class] = Editors.TEXT
        editMethod[Float::class] = Editors.TEXT
        editMethod[Double::class] = Editors.TEXT
        editMethod[String::class] = Editors.TEXT
        editMethod[ItemStack::class] = Editors.ITEM
        editMethod[MaterialData::class] = Editors.MATERIAL_DATA
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
        when (Adyeshach.settings.editorMode) {
            EditorMode.BOOK -> openByBook(player, entity)
            EditorMode.CHAT -> openByChat(player, entity)
        }
    }

    fun openByBook(player: Player, entity: EntityInstance) {
        val book = BookFormatter.writtenBook()
        book.addPages(ComponentSerializer.parse(TellrawJson.create()
                .append("  §1§l§n${entity.entityType.bukkitType}").newLine()
                .append("  §1${entity.id} ${if (entity.isTemporary()) "§7(Temporary)" else ""}").newLine()
                .append("").newLine()
                .append("  Type §7${if (entity.isPublic()) "PUBLIC" else "PRIVATE"}").newLine()
                .append("  Viewers §7${entity.viewPlayers.viewers.size} ").append("§c(?)").hoverText(entity.viewPlayers.viewers.joinToString("\n")).newLine()
                .append("  Controller §7${entity.getController().size} ").append("§c(?)").hoverText(entity.getController().joinToString("\n") { it.javaClass.name }).newLine()
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
            if (editor != null && editor.edit) {
                page.append("  §n${it.key.toDisplay()}").newLine()
                try {
                    page.append("   §c✘")
                            .clickCommand("/adyeshachapi edit reset ${entity.uniqueId} ${it.key}")
                            .hoverText("§nClick To Reset")
                    page.append(" §7${if (editor.onDisplay != null) editor.onDisplay!!.invoke(player, entity, it) else entity.getMetadata<Any>(it.key)}")
                            .clickCommand("/adyeshachapi edit meta ${entity.uniqueId} ${it.key}")
                            .hoverText("§nClick To Edit")
                            .newLine()
                } catch (t: Throwable) {
                    t.printStackTrace()
                    page.append(" §c§o<ERROR:${t.message}>").newLine()
                }
                if (++i == 6) {
                    i = 0
                    try {
                        book.addPage(page)
                    } catch (t: Throwable) {
                        book.addPage(TellrawJson.create().append("   §c<ERROR:${t.message}>").hoverText(page.toLegacyText()))
                    }
                    page = TellrawJson.create()
                }
            }
        }
        if (i > 0) {
            try {
                book.addPage(page)
            } catch (t: Throwable) {
                book.addPage(TellrawJson.create().append("   §c<ERROR:${t.message}>").hoverText(page.toLegacyText()))
            }
        }
        book.open(player)
    }

    fun openByChat(player: Player, entity: EntityInstance) {
        TellrawJson.create().run {
            repeat(127) { newLine() }
            send(player)
        }
        val json = TellrawJson.create()
                .newLine()
                .append("      §6§l§n${entity.entityType.bukkitType}").newLine()
                .append("      §6${entity.id} ${if (entity.isTemporary()) "§7(Temporary)" else ""}").newLine()
                .newLine()
                .append("      Type §7${if (entity.isPublic()) "PUBLIC" else "PRIVATE"}").newLine()
                .append("      Viewers §7${entity.viewPlayers.viewers.size} ").append("§c(?)").hoverText(entity.viewPlayers.viewers.joinToString("\n")).newLine()
                .append("      Pathfinder §7${entity.getController().size} ").append("§c(?)").hoverText(entity.getController().joinToString("\n") { it.javaClass.name }).newLine()
                .newLine().append("      ")
        var i = 0
        entity.listMetadata().sortedBy { it.key }.forEach {
            val editor = getEditor(it)
            if (editor != null && editor.edit) {
                json.append("§8[")
                try {
                    json.append("§7${it.key.toDisplay()}")
                            .clickCommand("/adyeshachapi edit meta ${entity.uniqueId} ${it.key}")
                            .hoverText("§7${if (editor.onDisplay != null) editor.onDisplay!!.invoke(player, entity, it) else entity.getMetadata<Any>(it.key)}")
                    json.append(" ")
                    json.append("§c✘")
                            .clickCommand("/adyeshachapi edit reset ${entity.uniqueId} ${it.key}")
                            .hoverText("§nClick To Reset")
                } catch (t: Throwable) {
                    json.append("§c§o<ERROR:${t.message}>")
                    if (t is NullPointerException) {
                        t.printStackTrace()
                    }
                }
                json.append("§8] ")
                if (++i == 3) {
                    i = 0
                    json.newLine().append("      ")
                }
            }
        }
        json.newLine().send(player)
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
        return if (source.length > 16) source.substring(0, source.length - (source.length - 10)) + "..." + source.substring(source.length - 7) else source
    }
}