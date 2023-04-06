package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.bukkit.BukkitRotation
import ink.ptms.adyeshach.core.entity.*
import ink.ptms.adyeshach.core.entity.type.*
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.EditType
import ink.ptms.adyeshach.module.editor.action.ActionGroup
import ink.ptms.adyeshach.module.editor.action.SimpleAction
import ink.ptms.adyeshach.module.editor.action.SimpleGroup
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
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
            val activeMeta = arrayListOf<Meta<*>>()
            fun read(cla: Class<*>) {
                // 专有数据 —— 搜索到 AdyMob 层时停止
                if (cla == AdyMob::class.java || cla == AdyEntityLiving::class.java || cla == AdyEntity::class.java) {
                    return
                }
                // 获取有效的元数据
                val meta = Adyeshach.api().getEntityMetadataRegistry().getEntityMeta(cla)
                    .filter { it in availableMeta }
                    .filter {
                        val check = it.isBool() || MetaEditor.getMetaEditor(it) != null || MetaEditor.getCustomMetaEditor(entity, it.key) != null
                        if (AdyeshachSettings.debug && !check) {
                            warning("Meta \"${it.key}\" of ${entity.entityType} is not supported by editor. (${it.def.javaClass.simpleName})")
                        }
                        check
                    }
                if (meta.isNotEmpty()) {
                    activeMeta += meta
                }
                // 向上搜索
                cla.interfaces.forEach { read(it) }
            }
            read(getAdyClass())
            activeMeta
        }.toGroups().toMutableList()
        // 附加数据
        applyExtras(groups)
        return groups
    }

    /**
     * 额外编辑器
     */
    fun applyExtras(groups: MutableList<SimpleGroup>) {
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
                // 画的方向
                kotlin.runCatching { SimpleAction.Meta("direction", EditType.AUTO, value = entity.getDirection()) }.getOrElse { SimpleAction.None },
            )
        }
        // 马
        if (entity is AdyHorse) {
            groups ah extras0(
                SimpleAction.Meta("color", EditType.AUTO, value = entity.getColor()),
                SimpleAction.Meta("style", EditType.AUTO, value = entity.getStyle()),
            )
        }
        // 羊
        if (entity is AdySheep) {
            groups ah extras0(
                SimpleAction.MetaBool("isSheared", value = entity.isSheared()),
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
        // 盔甲架
        if (entity is AdyArmorStand) {
            groups += SimpleGroup("angle-head", 8, BukkitRotation.HEAD.toActions())
            groups += SimpleGroup("angle-body", 8, BukkitRotation.BODY.toActions())
            groups += SimpleGroup("angle-left-arm", 8, BukkitRotation.LEFT_ARM.toActions())
            groups += SimpleGroup("angle-right-arm", 8, BukkitRotation.RIGHT_ARM.toActions())
            groups += SimpleGroup("angle-left-leg", 8, BukkitRotation.LEFT_LEG.toActions())
            groups += SimpleGroup("angle-right-leg", 8, BukkitRotation.RIGHT_LEG.toActions())
        }
        // 展示实体
        if (entity is AdyDisplay) {
            groups += SimpleGroup("display-translation", 8, Vector3.Meta.TRANSLATION.toActions(entity))
            groups += SimpleGroup("display-scale", 8, Vector3.Meta.SCALE.toActions(entity))
            groups += SimpleGroup("display-rotation-left", 8, Quat.Meta.ROTATION_LEFT.toActions(entity))
            groups += SimpleGroup("display-rotation-right", 8, Quat.Meta.ROTATION_RIGHT.toActions(entity))
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