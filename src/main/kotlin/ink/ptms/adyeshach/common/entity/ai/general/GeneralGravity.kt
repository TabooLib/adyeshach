package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Pathfinder
import io.izzel.taboolib.module.db.local.Local

/**
 * 实体重力逻辑（只受重力影响，不受碰撞箱及流体影响）
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class GeneralGravity(entity: EntityInstance) : Pathfinder(entity) {

    var isOnGround = false
        private set

    override fun shouldExecute(): Boolean {
        return true
    }

    override fun onTick() {
        val locEntity = entity.position.toLocation()
        val locDown = locEntity.clone().add(0.0, -1.0, 0.0)
        val y = locDown.y + locDown.block.boundingBox.maxY
        if (locEntity.y - y > 0.1) {
            entity.teleport(locEntity.clone().add(0.0, -0.1, 0.0))
            isOnGround = false
        } else {
            isOnGround = true
        }
    }
}