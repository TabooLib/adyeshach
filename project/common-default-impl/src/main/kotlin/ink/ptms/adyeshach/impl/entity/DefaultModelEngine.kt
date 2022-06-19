package ink.ptms.adyeshach.impl.entity

import ink.ptms.adyeshach.common.entity.ModelEngine
import org.bukkit.entity.Player
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.DefaultModelEngine
 *
 * @author 坏黑
 * @since 2022/6/19 21:58
 */
interface DefaultModelEngine : ModelEngine {

    override var modelEngineName: String
        get() = TODO("Not yet implemented")
        set(value) {}

    override var modelEngineUniqueId: UUID?
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun showModelEngine(viewer: Player): Boolean {
        TODO("Not yet implemented")
    }

    override fun hideModelEngine(viewer: Player): Boolean {
        TODO("Not yet implemented")
    }

    override fun refreshModelEngine(): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateModelEngineNameTag() {
        TODO("Not yet implemented")
    }
}