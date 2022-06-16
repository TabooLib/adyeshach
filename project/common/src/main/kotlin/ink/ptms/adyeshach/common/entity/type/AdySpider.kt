package ink.ptms.adyeshach.common.entity.type

/**
 * @author Arasple
 * @date 2020/8/5 22:35
 */
abstract class AdySpider : AdyMob() {

    open fun setClimbing(climbing: Boolean) {
        setMetadata("isClimbing", climbing)
    }

    open fun isClimbing(): Boolean {
        return getMetadata("isClimbing")
    }

}