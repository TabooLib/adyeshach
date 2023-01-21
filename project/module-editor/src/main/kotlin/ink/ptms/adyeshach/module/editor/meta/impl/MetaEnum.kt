package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.bukkit.BukkitCreeperState
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.Slots
import taboolib.platform.util.buildItem
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaEnum
 *
 * @author 坏黑
 * @since 2022/12/27 04:04
 */
@Suppress("DuplicatedCode")
class MetaEnum(val key: String, val enumClass: Class<*>, val useIndex: Boolean = true, val lowercase: Boolean = false) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        var enums = enumClass.enums().toList()
        if (enumClass == ChatColor::class.java) {
            enums = enums.filter { (it as ChatColor).toWool() != null }
        }
        player.openMenu<Linked<Any>>(player.lang("input-enums", enumClass.simpleName)) {
            rows(6)
            slots(Slots.CENTER)
            elements { enums }
            onGenerate { _, element, _, _ ->
                when (enumClass) {
                    ChatColor::class.java -> {
                        buildItem((element as ChatColor).toWool()!!) {
                            name = "$element${element.name}"
                            colored()
                        }
                    }
                    DyeColor::class.java -> {
                        buildItem((element as DyeColor).toWool()) {
                            name = "&7$element"
                            colored()
                        }
                    }
                    else -> {
                        buildItem(XMaterial.PAPER) {
                            name = "&7$element"
                            colored()
                        }
                    }
                }
            }
            onClick { _, element ->
                when {
                    // 爬行者
                    enumClass == BukkitCreeperState::class.java -> {
                        player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->${(element as BukkitCreeperState).value}")
                    }
                    // 序号
                    useIndex -> {
                        player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->${enums.indexOf(element)}")
                    }
                    // 名称
                    else -> {
                        val name = when {
                            lowercase -> element.toString().lowercase()
                            element is ChatColor -> element.name
                            else -> element
                        }
                        player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->$name")
                    }
                }
            }
            setNextPage(Slots.LINE_6_MIDDLE + 1) { _, _ ->
                buildItem(XMaterial.ARROW) {
                    name = "&7${player.lang("next")}"
                    colored()
                }
            }
            setPreviousPage(Slots.LINE_6_MIDDLE - 1) { _, _ ->
                buildItem(XMaterial.ARROW) {
                    name = "&7${player.lang("previous")}"
                    colored()
                }
            }
        }
    }

    companion object {

        val cacheEnums = ConcurrentHashMap<String, Array<out Any>>()

        /**
         * 获取所有枚举
         */
        fun Class<*>.enums(): Array<out Any> {
            return cacheEnums.computeIfAbsent(name) { enumConstants }
        }

        fun ChatColor.toWool(): XMaterial? {
            return when (this) {
                ChatColor.BLACK -> XMaterial.BLACK_WOOL
                ChatColor.DARK_BLUE -> XMaterial.BLUE_WOOL
                ChatColor.DARK_GREEN -> XMaterial.GREEN_WOOL
                ChatColor.DARK_AQUA -> XMaterial.CYAN_WOOL
                ChatColor.DARK_RED -> XMaterial.RED_WOOL
                ChatColor.DARK_PURPLE -> XMaterial.PURPLE_WOOL
                ChatColor.GOLD -> XMaterial.ORANGE_WOOL
                ChatColor.GRAY -> XMaterial.LIGHT_GRAY_WOOL
                ChatColor.DARK_GRAY -> XMaterial.GRAY_WOOL
                ChatColor.BLUE -> XMaterial.LIGHT_BLUE_WOOL
                ChatColor.GREEN -> XMaterial.LIME_WOOL
                ChatColor.AQUA -> XMaterial.LIGHT_BLUE_WOOL
                ChatColor.RED -> XMaterial.RED_WOOL
                ChatColor.LIGHT_PURPLE -> XMaterial.PINK_WOOL
                ChatColor.YELLOW -> XMaterial.YELLOW_WOOL
                ChatColor.WHITE -> XMaterial.WHITE_WOOL
                else -> null
            }
        }

        fun DyeColor.toWool(): XMaterial {
            return when (name) {
                "WHITE" -> XMaterial.WHITE_WOOL
                "ORANGE" -> XMaterial.ORANGE_WOOL
                "MAGENTA" -> XMaterial.MAGENTA_WOOL
                "LIGHT_BLUE" -> XMaterial.LIGHT_BLUE_WOOL
                "YELLOW" -> XMaterial.YELLOW_WOOL
                "LIME" -> XMaterial.LIME_WOOL
                "PINK" -> XMaterial.PINK_WOOL
                "GRAY" -> XMaterial.GRAY_WOOL
                "SILVER", "LIGHT_GRAY" -> XMaterial.LIGHT_GRAY_WOOL
                "CYAN" -> XMaterial.CYAN_WOOL
                "PURPLE" -> XMaterial.PURPLE_WOOL
                "BLUE" -> XMaterial.BLUE_WOOL
                "BROWN" -> XMaterial.BROWN_WOOL
                "GREEN" -> XMaterial.GREEN_WOOL
                "RED" -> XMaterial.RED_WOOL
                else -> XMaterial.BLACK_WOOL
            }
        }
    }
}