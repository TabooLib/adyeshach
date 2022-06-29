package ink.ptms.adyeshach.module

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.AdyeshachEditor
import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

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

    override fun openEditor(player: Player, entityInstance: EntityInstance) {
        TODO("Not yet implemented")
    }
}