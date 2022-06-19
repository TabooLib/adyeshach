package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
interface AdyDolphin : AdyMob {

    fun setFindTreasure(value: Boolean) {
        setMetadata("findTreasure", value)
    }

    fun isFindTreasure(): Boolean {
        return getMetadata("findTreasure")
    }

    fun setHasFish(value: Boolean) {
        setMetadata("hasFish", value)
    }

    fun isHasFish(): Boolean {
        return getMetadata("hasFish")
    }
}