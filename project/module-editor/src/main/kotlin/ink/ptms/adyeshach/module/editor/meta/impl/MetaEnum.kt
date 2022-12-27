package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.bukkit.BukkitCreeperState
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.DyeColor
import org.bukkit.entity.Player
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
class MetaEnum(val key: String, val enumClass: Class<*>, val useIndex: Boolean = false) : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        val enums = enumClass.enums()
        player.openMenu<Linked<Any>>(player.lang("input-enums", enumClass.simpleName)) {
            rows(6)
            slots(Slots.CENTER)
            elements { enums.toList() }
            onGenerate { _, element, _, _ ->
                if (enumClass == DyeColor::class.java) {
                    buildItem((element as DyeColor).toWool()) {
                        name = "&7$element"
                        colored()
                    }
                } else {
                    buildItem(XMaterial.PAPER) {
                        name = "&7$element"
                        colored()
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
                        player.chat("/adyeshach api ee adyeshach edit ${entity.uniqueId} m:$key->$element")
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

        fun DyeColor.toWool(): XMaterial {
            return when (this) {
                DyeColor.WHITE -> XMaterial.WHITE_WOOL
                DyeColor.ORANGE -> XMaterial.ORANGE_WOOL
                DyeColor.MAGENTA -> XMaterial.MAGENTA_WOOL
                DyeColor.LIGHT_BLUE -> XMaterial.LIGHT_BLUE_WOOL
                DyeColor.YELLOW -> XMaterial.YELLOW_WOOL
                DyeColor.LIME -> XMaterial.LIME_WOOL
                DyeColor.PINK -> XMaterial.PINK_WOOL
                DyeColor.GRAY -> XMaterial.GRAY_WOOL
                DyeColor.LIGHT_GRAY -> XMaterial.LIGHT_GRAY_WOOL
                DyeColor.CYAN -> XMaterial.CYAN_WOOL
                DyeColor.PURPLE -> XMaterial.PURPLE_WOOL
                DyeColor.BLUE -> XMaterial.BLUE_WOOL
                DyeColor.BROWN -> XMaterial.BROWN_WOOL
                DyeColor.GREEN -> XMaterial.GREEN_WOOL
                DyeColor.RED -> XMaterial.RED_WOOL
                DyeColor.BLACK -> XMaterial.BLACK_WOOL
            }
        }
    }
}