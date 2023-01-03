package ink.ptms.adyeshach.common.entity.path

/**
 * @author sky
 * @since 2020-08-13 16:34
 */
@Deprecated("Outdated but usable")
open class Result(val beginTime: Long, val scheduleTime: Long) {

    val endTime = System.currentTimeMillis()

    val waitTime = endTime - beginTime

    val costTime = endTime - scheduleTime
}