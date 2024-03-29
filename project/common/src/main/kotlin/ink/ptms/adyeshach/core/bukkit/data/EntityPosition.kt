package ink.ptms.adyeshach.core.bukkit.data

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.entity.type.errorBy
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

/**
 * @author sky
 * @since 2020-08-04 13:09
 */
data class EntityPosition(
    @Expose val world: World,
    @Expose var x: Double = 0.0,
    @Expose var y: Double = 0.0,
    @Expose var z: Double = 0.0,
    @Expose var yaw: Float = 0f,
    @Expose var pitch: Float = 0f,
) {

    fun blockX() = x.toInt()

    fun blockY() = y.toInt()

    fun blockZ() = z.toInt()

    fun add(x: Double, y: Double, z: Double): EntityPosition {
        this.x += x
        this.y += y
        this.z += z
        return this
    }

    fun add(position: EntityPosition): EntityPosition {
        this.x += position.x
        this.y += position.y
        this.z += position.z
        return this
    }

    fun subtract(x: Double, y: Double, z: Double): EntityPosition {
        this.x -= x
        this.y -= y
        this.z -= z
        return this
    }

    fun subtract(position: EntityPosition): EntityPosition {
        this.x -= position.x
        this.y -= position.y
        this.z -= position.z
        return this
    }

    fun isZero(): Boolean {
        return x == 0.0 && y == 0.0 && z == 0.0 && yaw == 0f && pitch == 0f
    }

    fun lengthSquared(): Double {
        return x * x + y * y + z * z
    }

    fun toVector(): Vector {
        return Vector(x, y, z)
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

    fun checkFinite(onError: Runnable = Runnable {}) {
        if (x.isInfinite() || y.isInfinite() || z.isInfinite() || yaw.isInfinite() || pitch.isInfinite()) {
            onError.run()
            errorBy("Position is infinite")
        }
        if (x.isNaN() || y.isNaN() || z.isNaN() || yaw.isNaN() || pitch.isNaN()) {
            onError.run()
            errorBy("Position is NaN")
        }
    }

    companion object {

        fun empty(): EntityPosition {
            if (Bukkit.getWorlds().isEmpty()) {
                errorBy("error-empty-worlds")
            }
            return EntityPosition(Bukkit.getWorlds()[0], 0.0, 0.0, 0.0, 0f, 0f)
        }

        fun fromLocation(location: Location): EntityPosition {
            return EntityPosition(location.world!!, location.x, location.y, location.z, location.yaw, location.pitch)
        }

        fun normalizeYaw(yaw: Float): Float {
            var y = yaw
            y %= 360.0f
            if (y >= 180.0f) {
                y -= 360.0f
            } else if (y < -180.0f) {
                y += 360.0f
            }
            return y
        }

        fun normalizePitch(pitch: Float): Float {
            var p = pitch
            if (p > 90.0f) {
                p = 90.0f
            } else if (p < -90.0f) {
                p = -90.0f
            }
            return p
        }
    }
}