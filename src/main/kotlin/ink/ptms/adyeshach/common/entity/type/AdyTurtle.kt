package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.module.nms.impl.Position

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyTurtle() : AdyEntityAgeable(EntityTypes.TURTLE) {

    init {
        val index = at(11500 to 18, 11400 to 17, 11300 to 15)
        registerMeta(index + 0, "hsaEgg", false)
        registerMeta(index + 1, "layingEgg", false)
        registerMeta(index + 2, "travelPos", Position(0, 0, 0))
        registerMeta(index + 3, "isGoingHome", false)
        registerMeta(index + 4, "isTraveling", false)
    }
}