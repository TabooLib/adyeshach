package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
abstract class AdyDolphin : AdyMob() {

    open fun setFindTreasure(value: Boolean) {
        setMetadata("findTreasure", value)
    }

    open fun isFindTreasure(): Boolean {
        return getMetadata("findTreasure")
    }

    open fun setHasFish(value: Boolean) {
        setMetadata("hasFish", value)
    }

    open fun isHasFish(): Boolean {
        return getMetadata("hasFish")
    }
}