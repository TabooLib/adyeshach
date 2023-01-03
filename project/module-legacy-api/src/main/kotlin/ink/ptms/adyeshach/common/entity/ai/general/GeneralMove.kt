package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.path.ResultNavigation
import org.bukkit.Location

/**
 * 实体移动逻辑
 *
 * @author sky
 * @since 2020-08-19 22:09
 */
@Deprecated("Outdated but usable")
class GeneralMove(entity: EntityInstance) : Controller(entity) {

    var speed = 0.0
    var target: Location? = null
    var pathType: PathType? = null
    var resultNavigation: ResultNavigation? = null

    override fun shouldExecute(): Boolean {
        return false
    }

    override fun onTick() {
    }
}