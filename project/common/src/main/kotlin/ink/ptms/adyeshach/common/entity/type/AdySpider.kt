package ink.ptms.adyeshach.common.entity.type

/**
 * @author Arasple
 * @date 2020/8/5 22:35
 */
interface AdySpider : AdyMob {

    fun setClimbing(climbing: Boolean) {
        setMetadata("isClimbing", climbing)
    }

    fun isClimbing(): Boolean {
        return getMetadata("isClimbing")
    }

}