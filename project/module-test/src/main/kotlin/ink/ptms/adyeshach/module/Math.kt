package ink.ptms.adyeshach.module

import org.bukkit.util.Vector
import kotlin.math.floor

/** 计算移动轨迹 */
fun calculateTrajectory(begin: Vector, deltaMovement: Vector): List<Vector> {
    // 初始化位置
    val position = begin.clone()
    // 记录移动轨迹
    val trajectory = mutableListOf<Vector>()
    // 计算移动轨迹
    while (square(deltaMovement.x) + square(deltaMovement.z) > 1E-6) {
        // 更新位置
        trajectory.add(position.add(Vector(deltaMovement.x, deltaMovement.y, deltaMovement.z)).clone())
        // 更新速度
        deltaMovement.multiply(0.8)
        deltaMovement.subtract(Vector(0.0, 0.08, 0.0))
    }
    return trajectory
}

/** 计算移动轨迹 */
fun getIntUnitsBetweenVectors(begin: Vector, end: Vector): List<Vector>  {
    // 初始化位置
    val position = begin.clone()
    // 记录移动轨迹
    val trajectory = mutableListOf<Vector>()
    // 计算移动向量
    val deltaMovement = end.clone().subtract(begin)
    // 计算移动距离
    val distance = deltaMovement.length()
    // 计算单位移动向量
    val unitMovement = deltaMovement.normalize()
    // 计算单位移动次数
    val numMoves = floor(distance).toInt()
    // 获取中间的所有单位
    for (i in 0 until numMoves) {
        trajectory.add(position.add(unitMovement).clone())
    }
    return trajectory
}

fun square(x: Double): Double {
    return x * x
}