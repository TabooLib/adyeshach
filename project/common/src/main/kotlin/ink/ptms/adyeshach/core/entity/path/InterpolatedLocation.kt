package ink.ptms.adyeshach.core.entity.path

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import org.spongepowered.math.GenericMath
import org.spongepowered.math.imaginary.Quaterniond
import org.spongepowered.math.vector.Vector3d
import taboolib.common.platform.function.warning
import java.util.*
import kotlin.math.acos

class InterpolatedLocation(val world: World, frames: Map<Int, Location> = HashMap()) {

    val frames = TreeMap(frames)
    var index = 0
    var end = 0

    fun addPoint(tick: Int, point: Vector) {
        if (frames.containsKey(tick)) {
            warning("Duplicate tick $tick -> $point")
        }
        frames[tick] = Location(world, point.x, point.y, point.z)
        end = tick
    }

    fun next(): Location? {
        return if (index <= end) get(index++) else null
    }

    fun get(tick: Int): Location {
        val left = frames.floorEntry(tick)
        return if (left == null) {
            frames.firstEntry().value
        } else if (left.key == tick) {
            left.value
        } else {
            val right = frames.higherEntry(tick)
            if (right == null) {
                left.value
            } else {
                val frac = (tick - left.key).toDouble() / (right.key - left.key)
                val position = right.value.clone().subtract(left.value).multiply(frac).add(left.value)
                val ld = Vector3d(left.value.yaw, left.value.pitch, 0f)
                val rd = Vector3d(right.value.yaw, right.value.pitch, 0f)
                val direction = Quaterniond.fromAngleRadAxis(frac * angle(ld, rd), ld.cross(rd)).rotate(rd)
                val p = if (right.value.y > left.value.y) 0.4 else 0.6
                val y = if (frac > p) right.value.y else left.value.y
                Location(world, position.x, y, position.z, direction.x().toFloat(), direction.y().toFloat())
            }
        }
    }

    private fun angle(a: Vector3d, b: Vector3d): Double {
        return acos(GenericMath.clamp(a.dot(b) / (a.length() * b.length()), -1.0, 1.0))
    }
}
