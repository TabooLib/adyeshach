package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Pathfinder
import io.izzel.taboolib.util.lite.Numbers

/**
 * 看向周围玩家
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class PathfinderLookAtPlayer(entity: EntityInstance) : Pathfinder(entity) {

    override fun shouldExecute(): Boolean {
        return Numbers.random(0.02) && !entity.isPathfinderMoving()
    }

    override fun onTick() {
        entity.viewPlayers.getViewers().minByOrNull { it.location.distance(entity.position.toLocation()) }?.let {
            entity.controllerLook(it.eyeLocation, smooth = true)
        }
    }
}