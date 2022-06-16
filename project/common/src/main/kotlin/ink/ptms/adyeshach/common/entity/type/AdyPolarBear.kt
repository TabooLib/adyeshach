package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyPolarBear : AdyEntityAgeable() {

    open fun isStanding(): Boolean {
        return getMetadata("isStanding")
    }

    open fun setStanding(value: Boolean) {
        setMetadata("isStanding", value)
    }
}