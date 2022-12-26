package ink.ptms.adyeshach.module.editor

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachEditor
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.module.editor.page.Page
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.getMetaFirstOrNull

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.DefaultAdyeshachEditor
 *
 * @author 坏黑
 * @since 2022/6/19 17:08
 */
object ChatEditor : AdyeshachEditor {

    @Awake(LifeCycle.LOAD)
    fun init() {
        Adyeshach.register(this)
    }

    override fun openEditor(player: Player, entityInstance: EntityInstance, forceEdit: Boolean) {
        EditPanel(player, entityInstance).open()
    }

    fun refresh(player: Player) {
        val page = player.getMetaFirstOrNull("adyeshach_last_open")?.value() as? Page
        val index = player.getMetaFirstOrNull("adyeshach_last_open_index")?.value() as? Int
        page?.open(index ?: 0)
    }
}