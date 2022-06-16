package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyTurtle : AdyEntityAgeable() {

    open fun isHasEgg(): Boolean {
        return getMetadata("hasEgg")
    }

    open fun setHasEgg(hasEgg: Boolean) {
        setMetadata("hasEgg", hasEgg)
    }

    open fun isLayingEgg(): Boolean {
        return getMetadata("layingEgg")
    }

    open fun setLayingEgg(layingEgg: Boolean) {
        setMetadata("layingEgg", layingEgg)
    }

    open fun isGoingHome(): Boolean {
        return getMetadata("isGoingHome")
    }

    open fun setGoingHome(goingHome: Boolean) {
        setMetadata("isGoingHome", goingHome)
    }

    open fun setTraveling(traveling: Boolean) {
        setMetadata("traveling",traveling)
    }

    open fun isTraveling():Boolean{
        return getMetadata("traveling")
    }
}