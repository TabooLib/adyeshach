package ink.ptms.adyeshach.common.bukkit.data

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

/**
 * @author sky
 * @since 2020-08-04 13:09
 */
@Deprecated("Outdated but usable")
class EntityPosition(val world: World, var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0, var yaw: Float = 0f, var pitch: Float = 0f) {

    fun v2(): ink.ptms.adyeshach.core.bukkit.data.EntityPosition {
        return ink.ptms.adyeshach.core.bukkit.data.EntityPosition(world, x, y, z, yaw, pitch)
    }

    fun add(x: Double, y: Double, z: Double): EntityPosition {
        this.x += x
        this.y += y
        this.z += z
        return this
    }

    fun subtract(x: Double, y: Double, z: Double): EntityPosition {
        this.x -= x
        this.y -= y
        this.z -= z
        return this
    }

    fun toLocation(): Location {
        return Location(world, x, y, z, yaw, pitch)
    }

    fun clone(): EntityPosition {
        return EntityPosition(world, x, y, z, yaw, pitch)
    }

    fun reset(): EntityPosition {
        x = 0.0
        y = 0.0
        z = 0.0
        yaw = 0f
        pitch = 0f
        return this
    }

    companion object {

        fun empty(): EntityPosition {
            return EntityPosition(Bukkit.getWorlds()[0], 0.0, 0.0, 0.0, 0f, 0f)
        }

        fun fromLocation(location: Location): EntityPosition {
            return EntityPosition(location.world!!, location.x, location.y, location.z, location.yaw, location.pitch)
        }
    }
}