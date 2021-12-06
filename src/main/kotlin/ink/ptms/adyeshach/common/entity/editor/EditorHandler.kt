package ink.ptms.adyeshach.common.entity.editor

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.AdyeshachSettings
import ink.ptms.adyeshach.common.bukkit.data.VectorNull
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
import org.bukkit.util.Vector
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.pluginVersion
import taboolib.common.platform.function.warning
import taboolib.common.util.nonPrimitive
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.TellrawJson
import taboolib.module.nms.getName
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

@Suppress("UNCHECKED_CAST")
object EditorHandler {

    /**
     * 针对特定类型的 MetaEditor 生成器
     * 使用 registerEntityMetaNatural 注册元数据模型时若未指定编辑器则从该容器中选取生成器生成对应的编辑器
     */
    internal val editorGenerator = HashMap<Class<*>, Consumer<MetaEditor<*>>>()

    /**
     * 储存玩家当前编辑盔甲架单位的实例以及所选择的角度
     */
    internal val armorStandLookup = ConcurrentHashMap<String, Pair<AdyArmorStand, ArmorStandAngle?>>()

    /**
     * 打开编辑器
     */
    fun open(player: Player, entity: EntityInstance) {
        when (AdyeshachSettings.editorMode) {
            EditorMode.BOOK -> openByBook(player, entity)
            EditorMode.CHAT -> openByChat(player, entity)
        }
    }

    /**
     * 打开书本编辑器
     */
    fun openByBook(player: Player, entity: EntityInstance) {
        val manager = if (entity.isPublic()) player.asLangText("editor-manager-public") else player.asLangText("editor-manager-private")
        player.sendBook {
            write(TellrawJson()
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
            )
            var page = TellrawJson()
            var i = 0
            entity.forEachMeta { meta, unused ->
                val editor = meta.editor
                if (editor != null && editor.editable) {
                    if (unused) {
                        page.append("  §8§m${meta.key.toLocale(player)}").newLine()
                    } else {
                        page.append("  §n${meta.key.toLocale(player)}").newLine()
                        try {
                            val display = (editor as MetaEditor<EntityInstance>).displayGenerator?.invoke(player, entity) ?: entity.getMetadata(meta.key)
                            page.append("   §c✘")
                                .runCommand("/adyeshachapi edit reset ${entity.uniqueId} ${meta.key}")
                                .hoverText(player.asLangText("editor-click-to-reset"))
                            page.append(" §7$display")
                                .runCommand("/adyeshachapi edit meta ${entity.uniqueId} ${meta.key}")
                                .hoverText(player.asLangText("editor-click-to-edit"))
                                .newLine()
                        } catch (t: Throwable) {
                            t.printStackTrace()
                            page.append(" §c§o<ERROR:${t.message}>").newLine()
                        }
                    }
                    if (++i == 6) {
                        i = 0
                        write(page)
                        page = TellrawJson()
                    }
                }
            }
            if (i > 0) {
                write(page)
            }
        }
    }

    /**
     * 打开聊天框编辑器
     */
    fun openByChat(player: Player, entity: EntityInstance) {
        TellrawJson().sendTo(adaptCommandSender(player)) { repeat(64) { newLine() } }
        val manager = if (entity.isPublic()) player.asLangText("editor-manager-public") else player.asLangText("editor-manager-private")
        val json = TellrawJson()
        if (entity.testing) {
            json.newLine()
                .append("      §c§l§n${entity.entityType.bukkitType}⚠").hoverText(player.asLangText("editor-testing", pluginVersion)).newLine()
                .append("      §c${entity.id} ${if (entity.isTemporary()) player.asLangText("editor-temporary") else ""}").newLine()
                .newLine()
        } else {
            json.newLine()
                .append("      §6§l§n${entity.entityType.bukkitType}").newLine()
                .append("      §6${entity.id} ${if (entity.isTemporary()) player.asLangText("editor-temporary") else ""}").newLine()
                .newLine()
        }
        // 基本信息
        json.append("      ")
        json.append("§8[§f${player.asLangText("editor-type")}: §7${manager}§8]")
        json.append(" ")
        json.append("§8[§f${player.asLangText("editor-viewer")}: §7${entity.viewPlayers.viewers.size}§8]")
            .hoverText(entity.viewPlayers.viewers.joinToString("\n").ifEmpty { "§7_" })
        json.append(" ")
        json.append("§8[§f${player.asLangText("editor-tags")}: §7${entity.getTags().size}§8")
            .hoverText(entity.getTags().joinToString("\n") { "${it.key} = ${it.value}" })
        json.append(" ")
        json.append("§8[§f${player.asLangText("editor-pathfinder")}: §7${entity.getController().size}")
            .hoverText(entity.getController().joinToString("\n") { it.javaClass.simpleName })
        json.append(" ")
        json.append("§a(+)§8]")
            .hoverText(player.asLangText("editor-click-to-edit"))
            .runCommand("/adyeshach controller ${entity.uniqueId}")
        json.newLine().append("      ")
        var len = 0
        var firstBool = true
        var firstUnused = true
        json.newLine().append("      ")
        entity.forEachMetaSorted(player) { meta, unused ->
            val editor = meta.editor
            if (editor == null) {
                warning("${meta.key}(${meta.def.javaClass}) 缺少编辑器")
            }
            if (editor != null && editor.editable) {
                try {
                    val display = (meta.editor as MetaEditor<EntityInstance>).displayGenerator?.invoke(player, entity) ?: entity.getMetadata(meta.key)
                    when {
                        unused -> {
                            if (firstUnused) {
                                firstUnused = false
                                if (len != 0) {
                                    json.newLine()
                                }
                                json.newLine().append("      ")
                                len = 0
                            }
                            json.append("§8[")
                            json.append("§8${meta.key.toLocale(player)}").hoverText("§7$display\n${player.asLangText("editor-unused")}")
                        }
                        meta.isBooleanType(player, entity) -> {
                            if (firstBool) {
                                firstBool = false
                                if (len != 0) {
                                    json.newLine()
                                }
                                json.newLine().append("      ")
                                len = 0
                            }
                            json.append("§8[")
                            val bool = if (display == player.asLangText("editor-meta-true")) "§a" else "§6"
                            json.append("$bool${meta.key.toLocale(player)}").hoverText("§7$display")
                        }
                        else -> {
                            json.append("§8[")
                            json.append("§7${meta.key.toLocale(player)}").hoverText("§7$display")
                        }
                    }
                    json.runCommand("/adyeshachapi edit meta ${entity.uniqueId} ${meta.key}")
                    if (!unused && !editor.hybrid) {
                        json.append(" ")
                        json.append("§c(R)")
                            .hoverText(player.asLangText("editor-click-to-reset"))
                            .runCommand("/adyeshachapi edit reset ${entity.uniqueId} ${meta.key}")
                    }
                } catch (t: NullPointerException) {
                    json.append("§c§o<ERROR_NULL:${meta.key}>").hoverText(meta.toString())
                    t.printStackTrace()
                } catch (t: Throwable) {
                    json.append("§c§o<ERROR:${t.message}>").hoverText(meta.toString())
                }
                json.append("§8] ")
                len += "[${meta.key.toLocale(player)}${if (unused || editor.hybrid) "" else " (R)"}]".realLength()
                if (len >= 52) {
                    len = 0
                    json.newLine().append("      ")
                }
            }
        }
        json.newLine().sendTo(adaptCommandSender(player))
    }

    @Awake(LifeCycle.INIT)
    private fun init() {
        // 基于字符串的类型
        AdyeshachAPI.registerEntityMetaEditorGenerator(
            Int::class.java.nonPrimitive(),
            Byte::class.java.nonPrimitive(),
            Float::class.java.nonPrimitive(),
            Double::class.java.nonPrimitive(),
            String::class.java,
            TextComponent::class.java
        ) {
            it.modify { player, entity ->
                player.edit(entity, entity.getMetadata(it.meta.key)) { value -> entity.setMetadata(it.meta.key, value) }
            }
            it.display { _, entity ->
                entity.getMetadata<Any>(it.meta.key).toString().minimize().ifEmpty { "§7_" }
            }
        }
        // 向量 (org.bukkit.util.Vector)
        AdyeshachAPI.registerEntityMetaEditorGenerator(Vector::class.java) {
            it.modify { player, entity ->
                entity.setMetadata(it.meta.key, Vector(player.location.blockX, player.location.blockY, player.location.blockZ))
                entity.openEditor(player)
            }
            it.display { _, entity ->
                entity.getMetadata<Vector>(it.meta.key).run { if (this is VectorNull) "_" else "$x $y $z" }
            }
        }
        // 布尔值
        AdyeshachAPI.registerEntityMetaEditorGenerator(Boolean::class.java.nonPrimitive()) {
            it.modify { player, entity ->
                entity.setMetadata(it.meta.key, !entity.getMetadata<Boolean>(it.meta.key))
                entity.openEditor(player)
            }
            it.display { player, entity ->
                entity.getMetadata<Boolean>(it.meta.key).toDisplay(player)
            }
        }
        // 物品
        AdyeshachAPI.registerEntityMetaEditorGenerator(ItemStack::class.java) {
            it.modify { player, entity ->
                player.openMenu<Basic>(player.asLangText("editor-item-input")) {
                    handLocked(false)
                    rows(1)
                    map("####@####")
                    set('#', XMaterial.BLACK_STAINED_GLASS_PANE) { name = "§f" }
                    set('@', it.meta.def as ItemStack)
                    onClick('#')
                    onClose { e ->
                        entity.setMetadata(it.meta.key, e.inventory.getItem(4) ?: ItemStack(Material.AIR))
                        entity.openEditor(player)
                    }
                }
            }
            it.display { player, entity ->
                entity.getMetadata<ItemStack>(it.meta.key).getName(player)
            }
        }
        // 材质
        AdyeshachAPI.registerEntityMetaEditorGenerator(MaterialData::class.java) {
            it.modify { player, entity ->
                player.openMenu<Basic>(player.asLangText("editor-item-input")) {
                    handLocked(false)
                    rows(1)
                    map("####@####")
                    set('#', XMaterial.BLACK_STAINED_GLASS_PANE) { name = "§f" }
                    set('@', (it.meta.def as MaterialData).toItemStack(1))
                    onClick('#')
                    onClose { e ->
                        try {
                            entity.setMetadata(it.meta.key, (e.inventory.getItem(4) ?: ItemStack(Material.AIR)).data!!)
                        } catch (t: Throwable) {
                            t.printStackTrace()
                        }
                        entity.openEditor(player)
                    }
                }
            }
            it.display { player, entity ->
                entity.getMetadata<MaterialData>(it.meta.key).toItemStack(1).getName(player)
            }
        }
    }

    @Schedule(period = 20)
    private fun e() {
        Bukkit.getOnlinePlayers().forEach {
            if (it.isOp && armorStandLookup.containsKey(it.name) && it.inventory.itemInMainHand.hasLore(it.asLangText("editor-armorstand-tool-lore"))) {
                it.sendLang("armor-stand-editor-2")
            }
        }
    }

    @SubscribeEvent
    private fun e(e: PlayerQuitEvent) {
        armorStandLookup.remove(e.player.name)
    }

    @SubscribeEvent
    private fun e(e: PlayerInteractEvent) {
        // 管理员且主手交互
        if (e.player.isOp && e.hand == EquipmentSlot.HAND) {
            // 编辑模式
            if (armorStandLookup.containsKey(e.player.name) && e.player.inventory.itemInMainHand.hasLore(e.player.asLangText("editor-armorstand-tool-lore"))) {
                e.isCancelled = true
                if (e.player.isSneaking) {
                    val bind = armorStandLookup[e.player.name]!!.first
                    val angle = armorStandLookup[e.player.name]!!.second ?: return
                    if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
                        angle.add.invoke(bind)
                    } else if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                        angle.subtract.invoke(bind)
                    }
                    e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
                } else {
                    e.player.openMenu<Basic>(e.player.asLangText("armor-stand-editor-name")) {
                        rows(3)
                        onBuild(async = true) { _, inv ->
                            ArmorStandAngle.values().forEach { inv.addItem(it.toItem()) }
                        }
                        onClick(lock = true) {
                            if (it.rawSlot >= 0 && it.rawSlot < ArmorStandAngle.values().size) {
                                val bind = armorStandLookup[e.player.name]!!.first
                                val angle = ArmorStandAngle.values()[it.rawSlot]
                                armorStandLookup[e.player.name] = bind to angle
                                e.player.closeInventory()
                                e.player.inventory.setItemInMainHand(e.player.inventory.itemInMainHand.modifyMeta<ItemMeta> {
                                    setDisplayName("§7${e.player.asLangText("editor-armorstand-tool-name", angle.name)}")
                                })
                                e.player.sendLang("armor-stand-editor-1", angle.name)
                                e.player.sendLang("armor-stand-editor-2")
                            }
                        }
                    }
                }
            }
        }
    }
}