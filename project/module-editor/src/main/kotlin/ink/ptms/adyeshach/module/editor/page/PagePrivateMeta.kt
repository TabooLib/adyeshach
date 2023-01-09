package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.bukkit.BukkitRotation
import ink.ptms.adyeshach.core.entity.*
import ink.ptms.adyeshach.core.entity.type.*
import ink.ptms.adyeshach.module.editor.EditPanel
import ink.ptms.adyeshach.module.editor.EditType
import ink.ptms.adyeshach.module.editor.action.Action
import ink.ptms.adyeshach.module.editor.action.ActionGroup
import ink.ptms.adyeshach.module.editor.action.SimpleAction
import ink.ptms.adyeshach.module.editor.action.SimpleGroup
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.entity.Player
import taboolib.common5.format
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
                    .filter { it.isBool() || MetaEditor.getMetaEditor(it) != null || MetaEditor.getCustomMetaEditor(entity, it.key) != null }
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
        extras(groups)
        return groups
    }

    fun extras(groups: MutableList<SimpleGroup>) {
        // 热带鱼
        if (entity is AdyTropicalFish) {
            groups ah extras0(
                SimpleAction.Meta("bodyColor", EditType.AUTO, value = entity.getBodyColor(), isResettable = false),
                SimpleAction.Meta("patternColor", EditType.AUTO, value = entity.getPatternColor(), isResettable = false),
                SimpleAction.Meta("pattern", EditType.AUTO, value = entity.getPattern(), isResettable = false),
            )
        }
        // 画
        if (entity is AdyPainting && minecraftVersion >= 11900) {
            groups ah extras0(
                SimpleAction.Meta("painting", EditType.AUTO, value = entity.getPainting(), isResettable = false),
                // 画的方向
                kotlin.runCatching { SimpleAction.Meta("direction", EditType.AUTO, value = entity.getDirection(), isResettable = false) }.getOrElse { SimpleAction.None },
            )
        }
        // 马
        if (entity is AdyHorse) {
            groups ah extras0(
                SimpleAction.Meta("color", EditType.AUTO, value = entity.getColor(), isResettable = false),
                SimpleAction.Meta("style", EditType.AUTO, value = entity.getStyle(), isResettable = false),
            )
        }
        // 经验球
        if (entity is AdyExperienceOrb) {
            groups ah extras0(
                SimpleAction.Meta("amount", EditType.SIGN, value = entity.getAmount(), isResettable = false),
            )
        }
        // 坠落方块
        if (entity is AdyFallingBlock) {
            groups ah extras0(
                SimpleAction.Meta("block", EditType.FALLING_BLOCK, value = entity.getMaterial(), isResettable = false),
            )
        }
        // 玩家
        if (entity is AdyHuman) {
            groups ah extras1(
                SimpleAction.MetaBool("is-sleeping", value = entity.isSleeping(), isResettable = false),
                SimpleAction.MetaBool("is-hide-from-tab-list", value = entity.isHideFromTabList, isResettable = false),
            )
            groups ah extras0(
                SimpleAction.Meta("player-name", EditType.SIGN, value = entity.getName(), isResettable = false),
                SimpleAction.Meta("player-texture", EditType.SIGN, value = entity.getTextureName(), isResettable = false),
                SimpleAction.Meta("player-ping", EditType.SIGN, value = entity.getPing(), isResettable = false),
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
    }

    fun BukkitRotation.toActions(): List<Action> {
        return listOf(Angle.Type.X.actions(this), Angle.Type.Y.actions(this), Angle.Type.Z.actions(this)).flatten()
    }

    class Angle(val value: Double, val type: Type, val rotation: BukkitRotation) : SimpleAction.Literal(if (value > 0) "&a+${value.format()}" else "&c${value.format()}", null) {

        enum class Type {

            X, Y, Z;

            fun actions(rotation: BukkitRotation): List<Action> {
                return listOf(10.0, 1.0, 0.5, 0.1, -10.0, -1.0, -0.5, -0.1).map { Angle(it, this, rotation) }
            }
        }

        override fun isCustomCommand(): Boolean {
            return true
        }

        override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
            entity as AdyArmorStand
            val eulerAngle = entity.getRotation(rotation)
            return when (type) {
                Type.X -> "adyeshach edit ${entity.uniqueId} m:${rotation.metaName}->${eulerAngle.x + value},${eulerAngle.y},${eulerAngle.z}"
                Type.Y -> "adyeshach edit ${entity.uniqueId} m:${rotation.metaName}->${eulerAngle.x},${eulerAngle.y + value},${eulerAngle.z}"
                Type.Z -> "adyeshach edit ${entity.uniqueId} m:${rotation.metaName}->${eulerAngle.x},${eulerAngle.y},${eulerAngle.z + value}"
            }
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