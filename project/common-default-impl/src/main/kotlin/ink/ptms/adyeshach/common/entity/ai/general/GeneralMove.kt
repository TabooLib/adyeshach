package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.util.path.ResultNavigation
import org.bukkit.Location

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
 *
 * @author 坏黑
 * @since 2022/6/28 12:53
 */
class GeneralMove(entity: EntityInstance) : Controller(entity) {

    var speed = 0.0
        set(value) {
            field = value.coerceAtMost(1.0)
        }

    var target: Location? = null
    var pathType: PathType? = null
    var resultNavigation: ResultNavigation? = null

    override fun shouldExecute(): Boolean {
        TODO("Not yet implemented")
    }

    override fun onTick() {
        TODO("Not yet implemented")
    }
}