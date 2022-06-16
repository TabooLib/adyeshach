package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyRaider : AdyMob() {

    open fun isCelebrating(): Boolean {
        return getMetadata("isCelebrating")
    }

    open fun setCelebrating(isCelebrating: Boolean) {
        setMetadata("isCelebrating", isCelebrating)
    }
}