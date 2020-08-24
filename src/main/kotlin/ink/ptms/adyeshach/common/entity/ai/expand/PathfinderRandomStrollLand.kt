package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Pathfinder
import ink.ptms.adyeshach.common.entity.ai.general.GeneralMove
import io.izzel.taboolib.util.lite.Numbers

/**
 * 随机移动（以原点为基础的随机移动，确保实体不会走出活跃范围）
 * 基于基础移动逻辑
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class PathfinderRandomStrollLand(entity: EntityInstance) : Pathfinder(entity) {

    override fun shouldExecute(): Boolean {
        return Numbers.random(0.01) && !getMove().isMoving
    }

    override fun onTick() {
        TODO("Not yet implemented")
    }

    fun getMove(): GeneralMove {
        return entity.pathfinder.first { it is GeneralMove } as GeneralMove
    }
}