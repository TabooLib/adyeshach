package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.Meta
import ink.ptms.adyeshach.core.entity.type.*
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.EditType
import ink.ptms.adyeshach.module.editor.action.ActionGroup
import ink.ptms.adyeshach.module.editor.action.SimpleAction
import ink.ptms.adyeshach.module.editor.action.SimpleGroup
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.page.PagePrivateMeta
 *
 * @author 坏黑
 * @since 2022/12/19 18:30
 */
@Suppress("DuplicatedCode")
class PagePrivateMeta(editor: EditPanel) : MetaPage(editor) {

    override fun subpage() = "private-meta"

    override fun groups(): List<ActionGroup> {
        val groups = privateMeta.computeIfAbsent(entity.entityType) {
            val availableMeta = getMetaList()
            val meta = arrayListOf<Meta<*>>()
            fun read(cla: Class<*>) {
                // 专有数据 —— 搜索到 AdyMob 层时停止
                if (cla == AdyMob::class.java || cla == AdyEntityLiving::class.java || cla == AdyEntity::class.java) {
                    return
                }
                meta += Adyeshach.api().getEntityMetadataRegistry().getEntityMeta(cla).filter { it in availableMeta }
                // 向上搜索
                cla.interfaces.forEach { read(it) }
            }
            read(getAdyClass())
            meta
        }.toGroups().toMutableList()
        // 附加数据
        extras(groups)
        return groups
    }

    fun extras(groups: MutableList<SimpleGroup>) {
        // 热带鱼
        if (entity is AdyTropicalFish) {
            groups ah extras0(
                SimpleAction.Meta("bodyColor", EditType.AUTO, value = entity.getBodyColor()),
                SimpleAction.Meta("patternColor", EditType.AUTO, value = entity.getPatternColor()),
                SimpleAction.Meta("pattern", EditType.AUTO, value = entity.getPattern()),
            )
        }
        // 画
        if (entity is AdyPainting && minecraftVersion >= 11900) {
            groups ah extras0(
                SimpleAction.Meta("painting", EditType.AUTO, value = entity.getPainting()),
                SimpleAction.Meta("direction", EditType.AUTO, value = entity.getDirection()),
            )
        }
        // 马
        if (entity is AdyHorse) {
            groups ah extras0(
                SimpleAction.Meta("color", EditType.AUTO, value = entity.getColor()),
                SimpleAction.Meta("style", EditType.AUTO, value = entity.getStyle()),
            )
        }
        // 经验球
        if (entity is AdyExperienceOrb) {
            groups ah extras0(
                SimpleAction.Meta("amount", EditType.SIGN, value = entity.getAmount()),
            )
        }
        // 坠落方块
        if (entity is AdyFallingBlock) {
            groups ah extras0(
                SimpleAction.Meta("block", EditType.FALLING_BLOCK, value = entity.getMaterial()),
            )
        }
        // 玩家
        if (entity is AdyHuman) {
            groups ah extras1(
                SimpleAction.MetaBool("is-sleeping", value = entity.isSleeping()),
                SimpleAction.MetaBool("is-hide-from-tab-list", value = entity.isHideFromTabList),
            )
            groups ah extras0(
                SimpleAction.Meta("player-name", EditType.SIGN, value = entity.getName()),
                SimpleAction.Meta("player-texture", EditType.SIGN, value = entity.getTextureName()),
                SimpleAction.Meta("player-ping", EditType.SIGN, value = entity.getPing()),
            )
        }
    }

    companion object {

        val privateMeta = ConcurrentHashMap<EntityTypes, List<Meta<*>>>()

        fun extras0(vararg meta: SimpleAction): SimpleGroup {
            return SimpleGroup.Extras("extras0", meta.toList())
        }

        fun extras1(vararg meta: SimpleAction): SimpleGroup {
            return SimpleGroup.Extras("extras1", meta.toList())
        }

        infix fun <T> MutableList<T>.ah(meta: T) {
            this.add(0, meta)
        }
    }
}