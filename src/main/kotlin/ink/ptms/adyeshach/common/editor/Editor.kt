package ink.ptms.adyeshach.common.editor

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachSettings
import ink.ptms.adyeshach.common.bukkit.data.VectorNull
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import ink.ptms.adyeshach.internal.listener.ListenerArmorStand
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.util.Vector
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.TellrawJson
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object Editor {

    val version = MinecraftVersion.majorLegacy
    val editMethod = HashMap<KClass<*>, EntityMetaable.MetaEditor>()
    val editArmorStand = ConcurrentHashMap<String, Pair<AdyArmorStand, ListenerArmorStand.Angle?>>()

    init {
        try {
            editMethod[Boolean::class] = EntityMetaable.MetaEditor()
                .modify { player, entity, meta ->
                    entity.setMetadata(meta.key, !entity.getMetadata<Boolean>(meta.key))
                    open(player, entity)
                }
                .display { _, entity, meta ->
                    entity.getMetadata<Boolean>(meta.key).toDisplay()
                }
            editMethod[Vector::class] = EntityMetaable.MetaEditor()
                .modify { player, entity, meta ->
                    entity.setMetadata(meta.key, Vector(player.location.blockX, player.location.blockY, player.location.blockZ))
                    open(player, entity)
                }
                .display { _, entity, meta ->
                    entity.getMetadata<Vector>(meta.key).run {
                        if (this is VectorNull) "_" else "$x $y $z"
                    }
                }
            editMethod[EulerAngle::class] = EntityMetaable.MetaEditor()
                .modify { player, entity, _ ->
                    editArmorStand[player.name] = entity as AdyArmorStand to null
                    player.inventory.takeItem(99) { it.hasLore("Adyeshach Tool") }
                    player.inventory.addItem(
                        buildItem(XMaterial.REDSTONE_TORCH) {
                            name = "&7Angle: &fNONE"
                            lore += "&8Adyeshach Tool"
                            shiny()
                            colored()
                        }
                    )
                    player.sendLang("editor-armorstand-tool")
                    player.closeInventory()
                }
                .display { _, entity, meta ->
                    entity.getMetadata<EulerAngle>(meta.key).run {
                        "${Coerce.format(x)} ${Coerce.format(y)} ${Coerce.format(z)}"
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
        } catch (t: Throwable) {
            t.printStackTrace()
        }
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
        when (AdyeshachSettings.editorMode) {
            EditorMode.BOOK -> openByBook(player, entity)
            EditorMode.CHAT -> openByChat(player, entity)
        }
    }

    fun openByBook(player: Player, entity: EntityInstance) {
        val manager = if (entity.isPublic()) player.asLangText("editor-manager-public") else player.asLangText("editor-manager-private")
        player.sendBook {
            writeRaw(
                TellrawJson()
                    .append("  §1§l§n${entity.entityType.bukkitType}").newLine()
                    .append("  §1${entity.id} ${if (entity.isTemporary()) player.asLangText("editor-temporary") else ""}").newLine()
                    .append("").newLine()
                    .append("  ${player.asLangText("editor-type")} §7$manager").newLine()
                    .append("  ${player.asLangText("editor-viewer")} §7${entity.viewPlayers.viewers.size} ").append("§c(?)")
                    .hoverText(entity.viewPlayers.viewers.joinToString("\n"))
                    .newLine()
                    .append("  ${player.asLangText("editor-tags")} §7${entity.getTags().size} ").append("§c(?)")
                    .hoverText(entity.getTags().joinToString("\n") { "${it.key} = ${it.value}" })
                    .newLine()
                    .append("  ${player.asLangText("editor-pathfinder")} §7${entity.getController().size} ").append("§c(?)")
                    .hoverText(entity.getController().joinToString("\n") { it.javaClass.name }).newLine()
                    .append("").newLine()
                    .append("   §7§oX ${entity.position.x}").newLine()
                    .append("   §7§oY ${entity.position.y}").newLine()
                    .append("   §7§oZ ${entity.position.z}").newLine()
                    .append("   §7§oYaw ${entity.position.yaw}").newLine()
                    .append("   §7§oPitch ${entity.position.pitch}").newLine()
                    .toRawMessage()
            )
            var page = TellrawJson()
            var i = 0
            entity.forEachMeta { meta, hide ->
                val editor = getEditor(meta)
                if (editor != null && editor.edit) {
                    if (hide) {
                        page.append("  §8§m${meta.key.toLocale(player)}").newLine()
                    } else {
                        page.append("  §n${meta.key.toLocale(player)}").newLine()
                        try {
                            val display = if (editor.onDisplay != null) editor.onDisplay!!.invoke(player, entity, meta) else entity.getMetadata<Any>(meta.key)
                            page.append("   §c✘")
                                .runCommand("/adyeshachapi edit reset ${entity.uniqueId} ${meta.key}")
                                .hoverText(player.asLangText("editor-click-to-reset").toString())
                            page.append(" §7$display")
                                .runCommand("/adyeshachapi edit meta ${entity.uniqueId} ${meta.key}")
                                .hoverText(player.asLangText("editor-click-to-edit").toString())
                                .newLine()
                        } catch (t: Throwable) {
                            t.printStackTrace()
                            page.append(" §c§o<ERROR:${t.message}>").newLine()
                        }
                    }
                    if (++i == 6) {
                        i = 0
                        try {
                            writeRaw(page.toRawMessage())
                        } catch (t: Throwable) {
                            writeRaw(TellrawJson().append("   §c<ERROR:${t.message}>").hoverText(page.toLegacyText()).toRawMessage())
                        }
                        page = TellrawJson()
                    }
                }
            }
            if (i > 0) {
                try {
                    writeRaw(page.toRawMessage())
                } catch (t: Throwable) {
                    writeRaw(TellrawJson().append("   §c<ERROR:${t.message}>").hoverText(page.toLegacyText()).toRawMessage())
                }
            }
        }
    }

    fun openByChat(player: Player, entity: EntityInstance) {
        TellrawJson().sendTo(adaptCommandSender(player)) {
            repeat(64) { newLine() }
        }
        val manager = if (entity.isPublic()) player.asLangText("editor-manager-public") else player.asLangText("editor-manager-private")
        val json = TellrawJson()
            .newLine()
            .append("      §6§l§n${entity.entityType.bukkitType}").newLine()
            .append("      §6${entity.id} ${if (entity.isTemporary()) player.asLangText("editor-temporary") else ""}").newLine()
            .newLine()
            .append("      ${player.asLangText("editor-type")} §7${manager}").newLine()
            .append("      ${player.asLangText("editor-viewer")} §7${entity.viewPlayers.viewers.size} ").append("§c(?)")
            .hoverText(entity.viewPlayers.viewers.joinToString("\n")).newLine()
            .append("      ${player.asLangText("editor-tags")} §7${entity.getTags().size} ").append("§c(?)")
            .hoverText(entity.getTags().joinToString("\n") { "${it.key} = ${it.value}" }).newLine()
            .append("      ${player.asLangText("editor-pathfinder")} §7${entity.getController().size} ").append("§c(?)")
            .hoverText(entity.getController().joinToString("\n") { it.javaClass.name })
            .append(" ")
            .append("§a(+)")
            .hoverText(player.asLangText("editor-click-to-edit").toString())
            .runCommand("/adyeshach controller ${entity.uniqueId}")
            .newLine()
            .newLine().append("      ")
        var i = 0
        entity.forEachMeta { meta, hide ->
            val editor = getEditor(meta)
            if (editor != null && editor.edit) {
                if (hide) {
                    json.append("§8[§8§m${meta.key.toLocale(player)}§8] ")
                } else {
                    json.append("§8[")
                    try {
                        val display = if (editor.onDisplay != null) editor.onDisplay!!.invoke(player, entity, meta) else entity.getMetadata<Any>(meta.key)
                        json.append("§7${meta.key.toLocale(player)}")
                            .runCommand("/adyeshachapi edit meta ${entity.uniqueId} ${meta.key}")
                            .hoverText("§7$display")
                        json.append(" ")
                        json.append("§c✘")
                            .runCommand("/adyeshachapi edit reset ${entity.uniqueId} ${meta.key}")
                            .hoverText(player.asLangText("editor-click-to-reset").toString())
                    } catch (t: NullPointerException) {
                        json.append("§c§o<ERROR_NULL:${meta.key}>").hoverText(meta.toString())
                        t.printStackTrace()
                    } catch (t: Throwable) {
                        json.append("§c§o<ERROR:${t.message}>").hoverText(meta.toString())
                    }
                    json.append("§8] ")
                }
                if (++i == AdyeshachSettings.editorMetaPerLine) {
                    i = 0
                    json.newLine().append("      ")
                }
            }
        }
        json.newLine().sendTo(adaptCommandSender(player))
    }

    fun toSimple(source: String): String {
        return if (source.length > 16) source.substring(0, source.length - (source.length - 10)) + "..." + source.substring(source.length - 7) else source
    }

    fun Boolean?.toDisplay(): String {
        return if (this == true) "§aTrue" else "§cFalse"
    }

    fun String?.toDisplay(): String {
        val builder = StringBuilder()
        toString().toCharArray().forEachIndexed { index, c ->
            when {
                index == 0 -> builder.append(c.uppercaseChar())
                c.isUpperCase() -> builder.append(" $c")
                else -> builder.append(c)
            }
        }
        return builder.toString()
    }

    fun String.toLocaleKey(): String {
        val builder = StringBuilder()
        toString().toCharArray().forEachIndexed { _, c ->
            when {
                c.isUpperCase() -> builder.append("-${c.lowercaseChar()}")
                else -> builder.append(c)
            }
        }
        return builder.toString()
    }

    fun String.toLocale(player: Player): String {
        return player.asLangText("editor-meta-${toLocaleKey()}").toString()
    }

    fun EntityInstance.forEachMeta(func: (EntityMetaable.Meta, Boolean) -> Unit) {
        listMetadata()
            .filter { it.index != -1 }
            .sortedBy { it.key }
            .forEach {
                func.invoke(it, HideMeta.isHideMeta(this, it))
            }
    }
}