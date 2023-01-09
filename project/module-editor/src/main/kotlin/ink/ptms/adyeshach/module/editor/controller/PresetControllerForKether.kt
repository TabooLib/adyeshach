package ink.ptms.adyeshach.module.editor.controller

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.controller.Controller
import ink.ptms.adyeshach.core.util.asLang
import ink.ptms.adyeshach.core.util.asLangList
import ink.ptms.adyeshach.impl.entity.controller.KetherControllerGenerator
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.Configuration
import taboolib.platform.util.buildItem

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.controller.PresetControllerForKether
 *
 * @author 坏黑
 * @since 2023/1/8 20:27
 */
class PresetControllerForKether(val id: String, val generator: KetherControllerGenerator) : PresetController(Configuration.empty()) {

    override val icon: ItemStack
        get() = buildItem(XMaterial.COMMAND_BLOCK_MINECART) {
            name = asLang("editor-controller-for-kether-name")
            lore.addAll(asLangList("editor-controller-for-kether-description", id))
        }

    override fun newInstance(entity: EntityInstance): Controller {
        return generator.generate(entity)
    }

    override fun toString(): String {
        return id.uppercase()
    }
}