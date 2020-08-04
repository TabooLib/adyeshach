package ink.ptms.adyeshach.common.position

import io.izzel.taboolib.internal.gson.annotations.Expose
import org.bukkit.Location
import org.bukkit.World

/**
 * @Author sky
 * @Since 2020-08-04 13:09
 */
class Position(
        @Expose
        var x: Double = 0.0,
        @Expose
        var y: Double = 0.0,
        @Expose
        var z: Double = 0.0,
        @Expose
        var yaw: Float = 0f,
        @Expose
        var pitch: Float = 0f
) {

    fun toLocation(world: World): Location {
        return Location(world, x, y, z, yaw, pitch)
    }

    fun clone(): Position {
        return Position(x, y, z, yaw, pitch)
    }

    fun reset(): Position {
        x = 0.0
        y = 0.0
        z = 0.0
        yaw = 0f
        pitch = 0f
        return this
    }

    companion object {

        fun empty(): Position {
            return Position(0.0, 0.0, 0.0, 0f, 0f)
        }

        fun fromLocation(location: Location): Position {
            return Position(location.x, location.y, location.z, location.yaw, location.pitch)
        }
    }
}