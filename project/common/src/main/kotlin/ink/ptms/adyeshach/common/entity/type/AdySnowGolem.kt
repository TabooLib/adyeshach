package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdySnowGolem : AdyMob() {

    open fun setPumpkinHat(value: Boolean) {
        setMetadata("hasPumpkinHat", value)
        setMetadata("hasNoPumpkinHat", !value)
    }

    open fun hasPumpkinHat(): Boolean {
        return getMetadata("hasPumpkinHat")
    }
}