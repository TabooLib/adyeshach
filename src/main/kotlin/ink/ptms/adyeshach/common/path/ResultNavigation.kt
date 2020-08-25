package ink.ptms.adyeshach.common.path

import io.izzel.taboolib.module.nms.impl.Position

/**
 * @Author sky
 * @Since 2020-08-13 16:34
 */
class ResultNavigation(val pointList: List<Position>, beginTime: Long, scheduleTime: Long) : Result(beginTime, scheduleTime)