package ink.ptms.adyeshach.module

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachEditor
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.StandardTags
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.module.lang.asLangText

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.AdyeshachEditor
 *
 * @author 坏黑
 * @since 2022/6/19 17:08
 */
object DefaultAdyeshachEditor : AdyeshachEditor {

    @Awake(LifeCycle.LOAD)
    fun init() {
        Adyeshach.register(this)
    }

    /**
     * \n  PLAYER (test) > 公有单位
     * \n  e449fcff-7351-4da1-b741-b0095162f4e9 [000001]
     * \n
     * \n  观察者 (0), 控制器 (0), 标签 (0), 关联 (0)
     * \n
     * \n  [公共特性] [公共数据] [专有数据]
     * \n
     * \n  [移动] [传送] [复制] [测试] [测试] [测试] [测试]
     * \n
     * \n
     * \n
     * \n
     * \n
     * \n  ^^^ 请展开对话框 ^^^
     * \n
     * \n
     * \n
     * \n
     * \n
     */
    override fun openEditor(player: Player, entityInstance: EntityInstance, forceEdit: Boolean) {
        // 清屏
        TellrawJson().sendTo(adaptPlayer(player)) { repeat(16) { newLine() } }
        // 标题
        player.sendMessage("    &6&l&n${entityInstance.entityType} &r&6(${entityInstance.id}) &e: &7${entityInstance.managerType(player)}".colored())
        // 序号
        TellrawJson().sendTo(adaptPlayer(player)) {
            append("    ")
            append("&8${entityInstance.uniqueId}").hoverText(player.node("copy")).suggestCommand(entityInstance.uniqueId)
            append(" ")
            append("&7[N]").hoverText(entityInstance.normalizeUniqueId.toString()).suggestCommand(entityInstance.normalizeUniqueId.toString())
        }
        player.sendMessage(" ")
        // 衍生单位
        val isDerived = entityInstance.hasPersistentTag(StandardTags.DERIVED)
        if (isDerived && !forceEdit) {
            return
        }
        // 基本数据
        TellrawJson().sendTo(adaptPlayer(player)) {
            append("    ")
            append("&7${player.node("viewer")} &f(0)".colored()).append(", ")
            append("&7${player.node("controller")} &f(0)".colored()).append(", ")
            append("&7${player.node("tags")} &f(0)".colored()).append(", ")
            append("&7${player.node("traits")} &f(0)".colored())
        }
        player.sendMessage(" ")
    }

    private fun Player.node(id: String): String {
        return adaptPlayer(this).asLangText("editor-$id")
    }

    private fun EntityInstance.managerType(player: Player): String {
        val builder = StringBuilder()
        when {
            hasTag(StandardTags.ISOLATED) -> {
                builder.append(player.node("manager-isolated"))
            }
            hasTag(StandardTags.IS_PUBLIC) -> {
                builder.append(player.node("manager-public"))
            }
            hasTag(StandardTags.IS_PRIVATE) -> {
                builder.append(player.node("manager-private"))
            }
        }
        if (hasTag(StandardTags.IS_TEMPORARY)) {
            builder.append(player.node("manager-temporary"))
        }
        if (hasTag(StandardTags.DERIVED)) {
            builder.append(player.node("manager-derived"))
        }
        builder.append(player.node("manager-object"))
        return builder.toString()
    }
}