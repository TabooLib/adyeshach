package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.Meta
import ink.ptms.adyeshach.core.entity.type.AdyEntity
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import ink.ptms.adyeshach.core.entity.type.AdyMob
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.action.ActionGroup
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.page.PagePublicMeta
 *
 * @author 坏黑
 * @since 2022/12/19 18:30
 */
class PagePublicMeta(editor: EditPanel) : MetaPage(editor) {

    override fun subpage() = "public-meta"

    override fun groups(): List<ActionGroup> {
        return publicMeta.computeIfAbsent(entity.entityType) {
            val availableMeta = getMetaList()
            val meta = arrayListOf<Meta<*>>()
            var f = false
            fun read(cla: Class<*>) {
                // 公有数据 —— 从 AdyMob 层开始搜索
                if (cla == AdyMob::class.java || cla == AdyEntityLiving::class.java || cla == AdyEntity::class.java) {
                    f = true
                }
                if (f) {
                    meta += Adyeshach.api().getEntityMetadataRegistry().getEntityMeta(cla).filter { it in availableMeta }
                }
                // 向上搜索
                cla.interfaces.forEach { read(it) }
            }
            read(getAdyClass())
            meta
        }.toGroups()
    }

    companion object {

        val publicMeta = ConcurrentHashMap<EntityTypes, List<Meta<*>>>()
    }
}