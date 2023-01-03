package ink.ptms.adyeshach.common.entity.path

import org.bukkit.util.Vector

/**
 * @author sky
 * @since 2020-08-13 16:34
 */
@Deprecated("Outdated but usable")
class ResultNavigation(val pointList: List<Vector>, beginTime: Long, scheduleTime: Long) : Result(beginTime, scheduleTime)