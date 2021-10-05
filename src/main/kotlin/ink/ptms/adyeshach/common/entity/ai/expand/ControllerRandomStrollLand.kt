package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.entity.path.PathFinderHandler
import ink.ptms.adyeshach.common.entity.path.Request
import ink.ptms.adyeshach.common.entity.path.ResultRandomPosition
import org.bukkit.Location
import taboolib.common.util.random

/**
 * 随机移动（实体会不断远离出生点）
 * 基于基础移动逻辑
 *
 * @author sky
 * @since 2020-08-19 22:09
 */
class ControllerRandomStrollLand(entity: EntityInstance) : Controller(entity) {

    override fun isAsync(): Boolean {
        return true
    }

    override fun shouldExecute(): Boolean {
        return random(0.01) && !entity!!.isControllerMoving()
    }

    override fun onTick() {
        PathFinderHandler.request(entity!!.position.toLocation(), entity.position.toLocation(), entity.entityType.getPathType(), Request.RANDOM_POSITION) {
            if (it is ResultRandomPosition && it.random != null) {
                entity.controllerMove(Location(entity.position.world, it.random.x, it.random.y, it.random.z), entity.entityType.getPathType(), entity.moveSpeed)
            }
        }
    }
}