package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller

/**
 * 随机移动（实体会不断远离出生点）
 * 基于基础移动逻辑
 *
 * @author sky
 * @since 2020-08-19 22:09
 */
@Deprecated("Outdated and unavailable")
class ControllerRandomStrollLand(entity: EntityInstance) : Controller(entity) {

    override fun isAsync(): Boolean {
        return true
    }

    override fun shouldExecute(): Boolean {
        return false
    }

    override fun onTick() {
    }
}