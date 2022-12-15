package ink.ptms.adyeshach.core.entity.path

import org.bukkit.World
import org.bukkit.util.Vector

/**
 * @author sky
 * @since 2020-08-13 16:34
 */
class ResultNavigation(val pointList: List<Vector>, beginTime: Long, scheduleTime: Long) : Result(beginTime, scheduleTime) {

    fun toInterpolated(world: World, moveSpeed: Double): InterpolatedLocation {
        val interpolatedLocation = InterpolatedLocation(world)
        var prevPoint: Vector? = null
        var tick = 0
        for (point in pointList) {
            interpolatedLocation.addPoint(tick, point)
            if (prevPoint != null) {
                val timeInterval = point.clone().subtract(prevPoint).length() / moveSpeed
                tick += timeInterval.toInt()
            }
            prevPoint = point
        }
        return interpolatedLocation
    }
}