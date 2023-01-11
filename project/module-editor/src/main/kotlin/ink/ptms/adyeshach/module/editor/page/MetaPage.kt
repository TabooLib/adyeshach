package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.Meta
import ink.ptms.adyeshach.core.entity.MetaMasked
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.EditType
import ink.ptms.adyeshach.module.editor.action.SimpleAction
import ink.ptms.adyeshach.module.editor.action.SimpleGroup

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.page.MetaPage
 *
 * @author 坏黑
 * @since 2022/12/27 20:58
 */
abstract class MetaPage(editor: EditPanel) : MultiplePage(editor) {

    protected fun getAdyClass(): Class<*> {
        return Adyeshach.api().getEntityTypeRegistry().getAdyClassFromEntityType(entity.entityType)
    }

    protected fun getMetaList(): List<Meta<*>> {
        return entity.getAvailableEntityMeta().filter { it.index != -1 }
    }

    protected fun List<Meta<*>>.toGroups(): List<SimpleGroup> {
        // 分组、排序
        val mapSorted = groupBy { it.group }.values.sortedBy { it.sumOf { m -> m.index } }
        // 合并
        return mapSorted.mapNotNull {
            val actions = it.mapNotNull { m ->
                try {
                    // 可切换的数据
                    if (m is MetaMasked<*> || m.def is Boolean) {
                        SimpleAction.MetaBool(m.key, value = entity.getMetadata(m.key))
                    } else {
                        SimpleAction.Meta(m.key, EditType.AUTO, value = entity.getMetadata(m.key))
                    }
                } catch (_: IllegalStateException) {
                    null
                }
            }
            if (actions.isEmpty()) {
                return@mapNotNull null
            }
            SimpleGroup(it.first().group, autoActionPerLine(), actions)
        }
    }
}