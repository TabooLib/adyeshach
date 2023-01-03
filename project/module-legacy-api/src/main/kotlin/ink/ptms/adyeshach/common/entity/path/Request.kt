package ink.ptms.adyeshach.common.entity.path

/**
 * @author sky
 * @since 2020-08-25 15:01
 */
@Deprecated("Outdated but usable")
enum class Request {

    NAVIGATION, RANDOM_POSITION;

    fun v2(): ink.ptms.adyeshach.core.entity.path.Request {
        return ink.ptms.adyeshach.core.entity.path.Request.values()[ordinal]
    }
}