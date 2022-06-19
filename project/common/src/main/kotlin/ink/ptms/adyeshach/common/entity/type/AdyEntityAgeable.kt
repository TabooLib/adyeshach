package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @since 2020-08-04 18:28
 */
@Suppress("SpellCheckingInspection")
interface AdyEntityAgeable : AdyMob {

    /**
     * 设置为幼年
     */
    fun setBaby(value: Boolean) {
        setMetadata("isBaby", value)
    }

    /**
     * 是否为幼年
     */
    fun isBaby(): Boolean {
        return getMetadata("isBaby")
    }
}