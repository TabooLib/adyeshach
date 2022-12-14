package ink.ptms.adyeshach.impl.util

import org.bukkit.Location
import org.bukkit.World
import org.spongepowered.math.GenericMath
import org.spongepowered.math.imaginary.Quaterniond
import org.spongepowered.math.vector.Vector3d
import java.util.*
import kotlin.math.acos

class InterpolatedLocation(val world: World, frames: Map<Int, Location>) {

    val frames = TreeMap(frames)

    fun getLocation(tick: Int): Location {
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
                Location(world, position.x, position.y, position.z, direction.x().toFloat(), direction.y().toFloat())
            }
        }
    }

    private fun angle(a: Vector3d, b: Vector3d): Double {
        return acos(GenericMath.clamp(a.dot(b) / (a.length() * b.length()), -1.0, 1.0))
    }
}
