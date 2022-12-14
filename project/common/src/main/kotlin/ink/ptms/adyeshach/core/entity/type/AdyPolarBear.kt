package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyPolarBear : AdyEntityAgeable {

    fun isStanding(): Boolean {
        return getMetadata("isStanding")
    }

    fun setStanding(value: Boolean) {
        setMetadata("isStanding", value)
    }
}