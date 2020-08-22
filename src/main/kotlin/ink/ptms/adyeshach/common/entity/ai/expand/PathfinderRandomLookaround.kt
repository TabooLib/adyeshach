package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Pathfinder
import io.izzel.taboolib.util.lite.Numbers

/**
 * 随机看向附近
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class PathfinderRandomLookaround(entity: EntityInstance) : Pathfinder(entity) {

    override fun shouldExecute(): Boolean {
        return Numbers.random(0.02)
    }

    override fun onTick() {
        TODO("Not yet implemented")
    }
}