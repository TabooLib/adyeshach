package ink.ptms.adyeshach.core.entity.path

import org.bukkit.util.Vector

/**
 * @author sky
 * @since 2020-08-13 16:34
 */
class ResultNavigation(val pointList: List<Vector>, beginTime: Long, scheduleTime: Long) : Result(beginTime, scheduleTime)