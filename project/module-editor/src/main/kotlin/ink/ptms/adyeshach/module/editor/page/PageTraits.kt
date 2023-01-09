package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import ink.ptms.adyeshach.impl.entity.trait.impl.*
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
        val groups = arrayListOf<ActionGroup>()
        // 基础特性
        groups += SimpleGroup(
            "traits0", 999, arrayListOf(
                SimpleAction.MetaBool("nitwit", value = entity.isNitwit, hasDescription = true),
                // 傻子不能编辑移动速度
                if (entity.isNitwit) SimpleAction.None else SimpleAction.Meta("move-speed", EditType.SIGN, value = entity.moveSpeed),
                // 可视距离
                SimpleAction.Meta("visible-distance", EditType.SIGN, value = entity.visibleDistance),
                SimpleAction.MetaBool("visible-after-loaded", value = entity.visibleAfterLoaded, hasDescription = true),
                // 模型
                if (entity is ModelEngine) SimpleAction.Meta("model-engine", EditType.SIGN, value = entity.modelEngineName, true) else SimpleAction.None
            )
        )
        // 生物特性
        if (entity is AdyEntityLiving) {
            groups += SimpleGroup(
                "traits1", 999, listOf(
                    SimpleAction.MetaBool("die", value = entity.isDie, hasDescription = true),
                    SimpleAction.Meta("equipment", EditType.EQUIPMENT, isResettable = false)
                )
            )
        }
        // 附加特性 1
        groups += SimpleGroup(
            "traits2", 999, listOf(
                SimpleAction.MetaTraitBool("sit", entity.isTraitSit()),
                SimpleAction.MetaTrait("title", entity.getTraitTitle().joinToString("\n")),
                SimpleAction.MetaTrait("command", entity.getTraitCommands().joinToString("\n")),
                SimpleAction.MetaTrait("view-condition", entity.getTraitViewCondition().joinToString("\n")),
            )
        )
        // 附加特性 2
        if (!entity.isNitwit) {
            groups += SimpleGroup(
                "traits3", 999, listOf(
                    SimpleAction.MetaTrait("patrols", entity.getTraitPatrolNodes().size),
                    SimpleAction.MetaTrait("patrols-wait-time", entity.getTraitPatrolWaitTime()),
                    SimpleAction.MetaTraitLiteral("patrols-update"),
                )
            )
        }
        return groups
    }
}