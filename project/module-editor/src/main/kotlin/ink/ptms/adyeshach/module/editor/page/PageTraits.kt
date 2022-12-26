package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.EditType
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
            SimpleAction.MetaBool("nitwit", value = entity.isNitwit, hasDescription = true),
            SimpleAction.Meta("move-speed", EditType.SIGN, value = entity.moveSpeed),
            SimpleAction.Meta("visible-distance", EditType.SIGN, value = entity.visibleDistance),
            SimpleAction.MetaBool("visible-after-loaded", value = entity.visibleAfterLoaded, hasDescription = true),
        )
        if (entity is ModelEngine) {
            traits0 += SimpleAction.Meta("model-engine", EditType.SIGN, value = entity.modelEngineName, hasDescription = true)
        }
        val groups = arrayListOf<ActionGroup>(SimpleGroup("traits0", 999, traits0))
        // 生物特性
        if (entity is AdyEntityLiving) {
            groups += SimpleGroup("traits1", 999, listOf(
                SimpleAction.MetaBool("die", value = entity.isDie, hasDescription = true),
                SimpleAction.Meta("equipment", EditType.EQUIPMENT, isResettable = false)
            ))
        }
        // 附加特性
        groups += SimpleGroup("traits2", 999, listOf(
            SimpleAction.Meta("title", EditType.SIGN, isResettable = false),
            SimpleAction.Meta("sit", EditType.SIGN, isResettable = false),
            SimpleAction.Meta("command", EditType.SIGN, isResettable = false),
            SimpleAction.Meta("patrols", EditType.SIGN, isResettable = false),
            SimpleAction.Meta("view-condition", EditType.SIGN, isResettable = false),
        ))
        return groups
    }
}