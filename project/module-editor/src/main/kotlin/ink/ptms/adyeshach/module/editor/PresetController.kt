package ink.ptms.adyeshach.module.editor

import com.google.gson.JsonPrimitive
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.controller.Controller
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.getItemStack
import taboolib.module.nms.ItemTagSerializer
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.Slots
import taboolib.platform.util.buildItem

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.PresetController
 *
 * @author 坏黑
 * @since 2022/12/28 19:54
 */
@Suppress("DuplicatedCode")
class PresetController(val root: ConfigurationSection) {

    /** 图标 */
    val icon = root.getItemStack("icon") ?: ItemStack(Material.STONE)

    /** 控制器生成器 */
    val cg = Adyeshach.api().getEntityControllerRegistry().getControllerGenerator(root.getString("instance").toString().substringBefore(':'))

    /** 控制器参数 */
    val args = root.getString("instance").toString().substringAfter(':', "").split(',').map { it.toPrimitive() }.toTypedArray()

    /** 异常 */
    var error = false

    /** 生成控制器 */
    fun newInstance(entity: EntityInstance): Controller? {
        return cg?.type?.invokeConstructor(entity, *args)
    }

    override fun toString(): String {
        return root.getString("instance").toString()
    }

    companion object {

        fun String.toPrimitive(): Any {
            return when {
                equals("true", true) -> true
                equals("false", true) -> false
                else -> ItemTagSerializer.deserializeData(JsonPrimitive(this)).unsafeData()
            }
        }

        fun open(player: Player, entity: EntityInstance) {
            player.openMenu<Linked<PresetController>>(player.lang("input-controller")) {
                rows(6)
                slots(Slots.CENTER)
                elements { ChatEditor.presetControllers.filter { it.cg != null } }
                onGenerate { _, element, _, _ ->
                    // 异常
                    if (element.error) {
                        buildItem(element.icon) {
                            material = Material.BARRIER
                            hideAll()
                        }
                    }
                    // 存在该控制器
                    else if (entity.getController().any { it.toString() == element.toString() }) {
                        buildItem(element.icon) {
                            shiny()
                            hideAll()
                        }
                    } else {
                        buildItem(element.icon) { hideAll() }
                    }
                }
                onClick { _, element ->
                    // 异常
                    if (element.error) {
                        return@onClick
                    }
                    // 存在该控制器
                    val find = entity.getController().firstOrNull { it.toString() == element.toString() }
                    if (find != null) {
                        entity.unregisterController(find)
                    } else {
                        try {
                            entity.registerController(element.newInstance(entity)!!)
                        } catch (ex: Throwable) {
                            element.error = true
                            ex.printStackTrace()
                        }
                    }
                    open(player, entity)
                }
                onClose {
                    ChatEditor.refresh(player)
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
    }
}