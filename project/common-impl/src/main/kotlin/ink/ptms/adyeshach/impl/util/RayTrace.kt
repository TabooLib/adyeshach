package ink.ptms.adyeshach.impl.util

import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * @author Arasple
 * @date 2020/8/25 15:12
 */
class RayTrace(val origin: Vector, val direction: Vector) {

    constructor(player: Player) : this(player.eyeLocation.toVector(), player.eyeLocation.direction)

    fun traces(distance: Double, accuracy: Double): Set<Vector> {
        return mutableSetOf<Vector>().let {
            var process = 0.0
            while (process <= distance) {
                it.add(distance(process))
                process += accuracy
            }
            it
        }
    }

    fun distance(distance: Double): Vector {
        return origin.clone().add(direction.clone().multiply(distance))
    }
}