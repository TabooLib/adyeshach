package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyTurtle() : AdyEntityAgeable(EntityTypes.TURTLE) {

    init {
        val index = at(11500 to 18, 11400 to 17, 11300 to 15)
        registerMeta(index + 0, "hasEgg", false)
        registerMeta(index + 1, "layingEgg", false)
        registerMeta(index + 3, "isGoingHome", false)
        registerMeta(index + 4, "isTraveling", false)
    }

    fun isHasEgg(): Boolean {
        return getMetadata("hasEgg");
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