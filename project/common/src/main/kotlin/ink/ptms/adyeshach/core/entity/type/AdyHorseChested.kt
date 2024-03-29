package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
interface AdyHorseChested : AdyHorseBase {

    override fun setHasChest(value: Boolean) {
        setMetadata("hasChest", value)
    }

    override fun isHasChest(): Boolean {
        return getMetadata("hasChest")
    }
}