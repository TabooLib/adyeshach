package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdySnowGolem : AdyMob {

    fun setPumpkinHat(value: Boolean) {
        setMetadata("hasPumpkinHat", value)
        setMetadata("hasNoPumpkinHat", !value)
    }

    fun hasPumpkinHat(): Boolean {
        return getMetadata("hasPumpkinHat")
    }
}