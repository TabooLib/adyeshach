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
import org.bukkit.util.EulerAngle
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.Vector
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.TellrawJson
import taboolib.module.nms.getName
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

object EditorHandler {

    /**
     * 针对特定类型的 MetaEditor 生成器
     * 使用 registerEntityMetaNatural 注册元数据模型时若未指定编辑器则从该容器中选取生成器生成对应的编辑器
     */
    internal val editorGenerator = HashMap<Class<*>, Consumer<MetaEditor>>()

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
            entity.forEachMeta { meta, hide ->
                val editor = meta.editor
                if (editor != null && editor.editable) {
                    if (hide) {
                        page.append("  §8§m${meta.key.toLocale(player)}").newLine()
                    } else {
                        page.append("  §n${meta.key.toLocale(player)}").newLine()
                        try {
                            val display = editor.displayGenerator?.invoke(player, entity) ?: entity.getMetadata(meta.key)
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
                        try {
                            write(page)
                        } catch (t: Throwable) {
                            write(TellrawJson().append("   §c<ERROR:${t.message}>").hoverText(page.toLegacyText()))
                        }
                        page = TellrawJson()
                    }
                }
            }
            if (i > 0) {
                try {
                    write(page)
                } catch (t: Throwable) {
                    write(TellrawJson().append("   §c<ERROR:${t.message}>").hoverText(page.toLegacyText()))
                }
            }
        }
    }

    /**
     * 打开聊天框编辑器
     */
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
        val isChineseSender = adaptPlayer(player).locale.startsWith("zh", ignoreCase = true)
        entity.forEachMeta { meta, hide ->
            val editor = meta.editor
            if (editor != null && editor.editable) {
                if (hide) {
                    json.append("§8[§8§m${meta.key.toLocale(player)}§8] ")
                } else {
                    json.append("§8[")
                    try {
                        val display = editor.displayGenerator?.invoke(player, entity) ?: entity.getMetadata(meta.key)
                        json.append("§7${meta.key.toLocale(player)}")
                            .runCommand("/adyeshachapi edit meta ${entity.uniqueId} ${meta.key}")
                            .hoverText("§7$display")
                        json.append(" ")
                        json.append("§c✘")
                            .runCommand("/adyeshachapi edit reset ${entity.uniqueId} ${meta.key}")
                            .hoverText(player.asLangText("editor-click-to-reset"))
                    } catch (t: NullPointerException) {
                        json.append("§c§o<ERROR_NULL:${meta.key}>").hoverText(meta.toString())
                        t.printStackTrace()
                    } catch (t: Throwable) {
                        json.append("§c§o<ERROR:${t.message}>").hoverText(meta.toString())
                    }
                    json.append("§8] ")
                }
                // 中文语言发送者
                if (isChineseSender) {
                    i += meta.key.toLocale(player).length
                    if (i >= 15) {
                        i = 0
                        json.newLine().append("      ")
                    }
                } else {
                    if (++i == AdyeshachSettings.editorMetaPerLine) {
                        i = 0
                        json.newLine().append("      ")
                    }
                }
            }
        }
        json.newLine().sendTo(adaptCommandSender(player))
    }

    @Awake(LifeCycle.INIT)
    private fun init() {
        // 基于字符串的类型
        AdyeshachAPI.registerEntityMetaEditorGenerator(
            Int::class.java,
            Byte::class.java,
            Float::class.java,
            Double::class.java,
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
        // 向量 (taboolib.common.util.Vector)
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
        AdyeshachAPI.registerEntityMetaEditorGenerator(Boolean::class.java) {
            it.modify { player, entity ->
                entity.setMetadata(it.meta.key, !entity.getMetadata<Boolean>(it.meta.key))
                entity.openEditor(player)
            }
            it.display { _, entity ->
                entity.getMetadata<Boolean>(it.meta.key).toDisplay()
            }
        }
        // 物品
        AdyeshachAPI.registerEntityMetaEditorGenerator(ItemStack::class.java) {
            it.modify { player, entity ->
                player.openMenu<Basic>(player.asLangText("editor-item-input")) {
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
        // 盔甲架角度
        AdyeshachAPI.registerEntityMetaEditorGenerator(EulerAngle::class.java) {
            it.modify { player, entity ->
                armorStandLookup[player.name] = entity as AdyArmorStand to null
                player.inventory.takeItem(99) { item -> item.hasLore(player.asLangText("editor-armorstand-tool-lore")) }
                player.giveItem(buildItem(XMaterial.REDSTONE_TORCH) {
                    name = "&7${player.asLangText("editor-armorstand-tool-name", "NONE")}"
                    lore += "&8${player.asLangText("editor-armorstand-tool-lore")}"
                    shiny()
                    colored()
                })
                player.sendLang("editor-armorstand-tool")
                player.closeInventory()
            }
            it.display { _, entity ->
                entity.getMetadata<EulerAngle>(it.meta.key).run { "${Coerce.format(x)} ${Coerce.format(y)} ${Coerce.format(z)}" }
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
                                    setDisplayName("&7${e.player.asLangText("editor-armorstand-tool-name", angle.name)}")
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