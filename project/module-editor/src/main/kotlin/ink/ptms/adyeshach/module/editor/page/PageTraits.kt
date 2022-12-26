package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.action.ActionGroup
import ink.ptms.adyeshach.module.editor.action.SimpleAction
import ink.ptms.adyeshach.module.editor.action.SimpleGroup

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.page.PageTraits
 *
 * @author 坏黑
 * @since 2022/12/19 18:30
 */
class PageTraits(editor: EditPanel) : MultiplePage(editor) {

    override fun subpage() = "traits"

    override fun groups(): List<ActionGroup> {
        val traits0 = arrayListOf(
            SimpleAction.MetaBool("nitwit", entity.isNitwit, true),
            SimpleAction.Meta("move-speed", "t"),
            SimpleAction.Meta("visible-distance", "t"),
            SimpleAction.MetaBool("visible-after-loaded", entity.visibleAfterLoaded, true),
        )
        if (entity is ModelEngine) {
            traits0 += SimpleAction.Meta("model-engine", "t", true)
        }
        val groups = arrayListOf<ActionGroup>(SimpleGroup("group-traits0", autoActionPerLine(), traits0))
        // 生物特性
        if (entity is AdyEntityLiving) {
            groups += SimpleGroup("group-traits1", autoActionPerLine(), listOf(
                SimpleAction.MetaBool("die", entity.isDie, true),
                SimpleAction.Meta("equipment", "equipment", isResettable = false)
            ))
        }
        return groups
    }
}