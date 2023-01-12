package ink.ptms.adyeshach.impl.entity.controller

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.controller.Controller
import ink.ptms.adyeshach.core.entity.path.PathFinderHandler
import ink.ptms.adyeshach.core.entity.path.Request
import ink.ptms.adyeshach.core.entity.path.ResultRandomPosition

class ControllerRandomStrollLand(entity: EntityInstance, @Expose val probability: Double) : Controller(entity) {

    constructor(entity: EntityInstance) : this(entity, 0.001)

    override fun id(): String {
        return "RANDOM_STROLL_LAND"
    }

    override fun group(): String {
        return "WALK"
    }

    override fun shouldExecute(): Boolean {
        // 当满足概率时触发控制器
        return entity != null && entity!!.random().nextDouble() < probability;
    }

    override fun continueExecute(): Boolean {
        return false
    }

    override fun start() {
        if (entity != null) {
            // 请求一个随机位置
            PathFinderHandler.request(entity!!.getLocation(), request = Request.RANDOM_POSITION) {
                // 移动到随机位置
                entity!!.controllerMoveTo((it as ResultRandomPosition).random.toLocation(entity!!.world))
            }
        }
    }

    override fun toString(): String {
        return "${id()}:${"%.2f".format(probability)}"
    }
}