package ink.ptms.adyeshach.core.entity.path

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import org.spongepowered.math.GenericMath
import org.spongepowered.math.imaginary.Quaterniond
import org.spongepowered.math.vector.Vector3d
import taboolib.common.platform.function.warning
import java.util.*
import kotlin.math.abs
import kotlin.math.acos

open class InterpolatedLocation(val world: World, val target: Location, frames: Map<Int, Location> = HashMap()) {

    val frames = TreeMap(frames)

    /** 序号 */
    var index = 1

    /** 长度 */
    var length = 0
        private set

    /** 当前 y 轴 */
    var cy = -1.0
        private set

    var d = 0
        private set

    /** 是否到达目的地 */
    open fun isArrived(): Boolean {
        return index >= length
    }

    /** 重置进度 */
    open fun reset(): InterpolatedLocation {
        index = 1
        cy = -1.0
        d = 0
        return this
    }

    /** 获取下一个位置，但不会改变进度 */
    open fun peek(): Location? {
        return if (isArrived()) null else get(index)
    }

    /** 获取下一个位置，并且改变进度 */
    open fun next(): Location? {
        if (isArrived()) {
            return null
        }
        val next = peek()
        if (next != null) {
            index++
        }
        return next
    }

    /** 获取指定进度的位置 */
    open fun get(tick: Int): Location? {
        val left = frames.floorEntry(tick)
        if (left == null) {
            return frames.firstEntry().value
        } else if (left.key == tick) {
            return left.value
        } else {
            val right = frames.higherEntry(tick)
            if (right == null) {
                return left.value
            } else {
                val frac = (tick - left.key).toDouble() / (right.key - left.key)
                // 坐标插值
                val position = right.value.clone().subtract(left.value).multiply(frac).add(left.value)
                val p = if (left.value.y < right.value.y) 0.2 else 0.8
                val y = if (frac > p) right.value.y else left.value.y
                // y 轴变动
                if (cy != y) {
                    cy = y
                    // 完整方块，停顿 1 游戏刻
                    if (abs(left.value.y - right.value.y) > 0.9 && d++ < 2) {
                        return null
                    }
                } else {
                    d = 0
                }
                // 视角插值
                val ld = Vector3d(left.value.yaw, left.value.pitch, 0f)
                val rd = Vector3d(right.value.yaw, right.value.pitch, 0f)
                val direction = Quaterniond.fromAngleRadAxis(frac * angle(ld, rd), ld.cross(rd)).rotate(rd)
                return Location(world, position.x, y, position.z, direction.x().toFloat(), direction.y().toFloat())
            }
        }
    }

    /** 添加一个位置 */
    open fun addPoint(tick: Int, point: Vector) {
        if (frames.containsKey(tick)) {
            warning("Duplicate tick $tick -> $point")
        }
        frames[tick] = Location(world, point.x, point.y, point.z)
        length = tick
    }

    private fun angle(a: Vector3d, b: Vector3d): Double {
        return acos(GenericMath.clamp(a.dot(b) / (a.length() * b.length()), -1.0, 1.0))
    }
}
