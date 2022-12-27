package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.Meta
import ink.ptms.adyeshach.core.entity.type.AdyEntity
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import ink.ptms.adyeshach.core.entity.type.AdyMob
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.action.ActionGroup

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.page.PagePublicMeta
 *
 * @author 坏黑
 * @since 2022/12/19 18:30
 */
class PagePublicMeta(editor: EditPanel) : MultiplePage(editor) {

    override fun subpage() = "public-meta"

    override fun groups(): List<ActionGroup> {
        val availableMeta = entity.getAvailableEntityMeta()
        val meta = arrayListOf<Meta<*>>()
        var f = false
        fun read(cla: Class<*>, level: Int = 0) {
            // 公有数据 —— 从 AdyMob 层开始搜索
            if (cla == AdyMob::class.java || cla == AdyEntityLiving::class.java || cla == AdyEntity::class.java) {
                f = true
            }
            if (f) {
                meta += Adyeshach.api().getEntityMetadataRegistry().getEntityMeta(cla).filter { it in availableMeta }
            }
            // 向上搜索
            cla.interfaces.forEach {
                read(it, level + 1)
            }
        }
        read(entity.javaClass)
        return emptyList()
    }
}