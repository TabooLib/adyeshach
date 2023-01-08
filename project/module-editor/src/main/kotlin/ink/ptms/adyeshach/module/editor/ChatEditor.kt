package ink.ptms.adyeshach.module.editor

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachEditor
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.controller.PresetController
import ink.ptms.adyeshach.module.editor.controller.PresetControllerForKether
import ink.ptms.adyeshach.module.editor.page.Page
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.util.resettableLazy
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.mapListAs
import taboolib.platform.util.getMetaFirstOrNull

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.DefaultAdyeshachEditor
 *
 * @author 坏黑
 * @since 2022/6/19 17:08
 */
object ChatEditor : AdyeshachEditor {

    @Config("editor/controller.yml")
    lateinit var controller: Configuration
        private set

    /** 预设控制器列表 */
    val presetControllers by resettableLazy {
        controller.reload()
        val list = arrayListOf<PresetController>()
        // 加载预设控制器
        list += controller.mapListAs("Controllers") { Configuration.fromMap(it) }.map { PresetController(it) }
        // 加载 Kether 控制器
        Adyeshach.api().getEntityControllerRegistry().getControllerGenerator().forEach { (k, v) ->
            if (k.startsWith("inner:")) {
                list += PresetControllerForKether(k.substringAfter("inner:"), v)
            }
        }
        list
    }

    @Awake(LifeCycle.LOAD)
    fun init() {
        Adyeshach.register(this)
    }

    override fun openEditor(player: Player, entityInstance: EntityInstance, forceEdit: Boolean) {
        EditPanel(player, entityInstance).open()
    }

    fun refresh(player: Player) {
        val page = player.getMetaFirstOrNull("adyeshach_last_open")?.value() as? Page
        val index = player.getMetaFirstOrNull("adyeshach_last_open_index")?.value() as? Int
        page?.open(index ?: 0)
    }
}