package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyRaider : AdyMob {

    fun isCelebrating(): Boolean {
        return getMetadata("isCelebrating")
    }

    fun setCelebrating(isCelebrating: Boolean) {
        setMetadata("isCelebrating", isCelebrating)
    }
}