package ink.ptms.adyeshach.core.entity.path

import ink.ptms.adyeshach.core.AdyeshachSettings
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

/**
 * @author sky
 * @since 2020-08-13 16:34
 */
class ResultNavigation(val pointList: MutableList<Vector>, beginTime: Long, scheduleTime: Long) : Result(beginTime, scheduleTime) {

    /**
     * 将寻路结果转换为插值坐标
     *
     * @param world 世界
     * @param moveSpeed 移动速度
     * @return 插值坐标
     */
    fun toInterpolated(world: World, moveSpeed: Double, moveTarget: Location): InterpolatedLocation {
        val interpolatedLocation = InterpolatedLocation(world, moveTarget)
        var prevPoint: Vector? = null
        var tick = 0
        for (point in pointList) {
            if (prevPoint != null) {
                val timeInterval = point.clone().subtract(prevPoint).length() / moveSpeed
                tick += timeInterval.toInt()
            }
            interpolatedLocation.addPoint(tick, point.clone().add(Vector(0.5, 0.0, 0.5)))
            prevPoint = point
        }
        // 调试模式下将显示路径节点
        if (AdyeshachSettings.debug) {
            for (point in pointList) {
                world.spawnParticle(org.bukkit.Particle.VILLAGER_HAPPY, point.x + 0.5, point.y, point.z + 0.5, 10, 0.0, 0.0, 0.0, 0.0)
            }
            (0..interpolatedLocation.end).forEach {
                val next = interpolatedLocation.get(it)
                world.spawnParticle(org.bukkit.Particle.FLAME, next.x, next.y, next.z, 2, 0.0, 0.0, 0.0, 0.0)
            }
        }
        return interpolatedLocation
    }
}