package ink.ptms.adyeshach.common.path

import io.izzel.taboolib.module.nms.impl.Position

/**
 * @Author sky
 * @Since 2020-08-13 16:34
 */
open class Result(
        /**
         * 请求发起时间
         */
        val beginTime: Long,
        /**
         * 请求处理时间
         */
        val scheduleTime: Long
) {

    /**
     * 请求处理结束时间
     */
    val endTime = System.currentTimeMillis()

    /**
     * 自 ”请求发起“ 至 ”处理结束“ 的时间损耗
     */
    val waitTime = endTime - beginTime

    /**
     * 自 "处理请求" 至 "处理结束" 的时间损耗
     */
    val costTime = endTime - scheduleTime
}