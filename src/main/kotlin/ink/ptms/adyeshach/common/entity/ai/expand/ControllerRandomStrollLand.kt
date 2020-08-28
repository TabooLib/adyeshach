package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.api.Settings
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.path.PathFinderProxy
import ink.ptms.adyeshach.common.path.Request
import ink.ptms.adyeshach.common.path.ResultRandomPosition
import io.izzel.taboolib.util.lite.Numbers
import org.bukkit.Location

/**
 * 随机移动（以原点为基础的随机移动，确保实体不会走出活跃范围）
 * 基于基础移动逻辑
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class ControllerRandomStrollLand(entity: EntityInstance) : Controller(entity) {

    override fun shouldExecute(): Boolean {
        return Numbers.random(0.001) && !entity.isPathfinderMoving()
    }

    override fun onTick() {
        PathFinderProxy.request(entity.position.toLocation(), entity.position.toLocation(), entity.entityType.getPathType(), Request.RANDOM_POSITION) {
            if (it is ResultRandomPosition && it.random != null) {
                entity.controllerMove(Location(entity.position.world, it.random.x, it.random.y, it.random.z), entity.entityType.getPathType(), speed = 0.1)
            }
        }
    }
}