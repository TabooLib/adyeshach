package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyTurtle : AdyEntityAgeable {

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
        setMetadata("traveling", traveling)
    }

    fun isTraveling(): Boolean {
        return getMetadata("traveling")
    }
}