package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
class AdyTurtle(v2: ink.ptms.adyeshach.core.entity.EntityInstance):  AdyEntityAgeable(EntityTypes.TURTLE, v2) {

    fun isHasEgg(): Boolean {
        return getMetadata("hasEgg")
    }

    fun setHasEgg(hasEgg: Boolean) {
        setMetadata("hasEgg", hasEgg)
    }

    fun isLayingEgg(): Boolean {
        return getMetadata("layingEgg")
    }

    fun setLayingEgg(layingEgg: Boolean) {
        setMetadata("layingEgg", layingEgg)
    }

    fun isGoingHome(): Boolean {
        return getMetadata("isGoingHome")
    }

    fun setGoingHome(goingHome: Boolean) {
        setMetadata("isGoingHome", goingHome)
    }

    fun setTraveling(traveling: Boolean) {
        setMetadata("traveling",traveling)
    }

    fun isTraveling():Boolean{
        return getMetadata("traveling")
    }
}