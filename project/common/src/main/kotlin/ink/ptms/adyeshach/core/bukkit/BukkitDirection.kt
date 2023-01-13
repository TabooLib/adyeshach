package ink.ptms.adyeshach.core.bukkit

import ink.ptms.adyeshach.core.util.ifloor

/**
 * @author Arasple
 * @date 2020/8/6 17:49
 */
enum class BukkitDirection {

    DOWN,

    UP,

    NORTH,

    SOUTH,

    WEST,

    EAST;

    companion object {

        val by2D = listOf(SOUTH, WEST, NORTH, EAST)

        fun of(index: Int): BukkitDirection {
            return values()[index]
        }

        fun fromYaw(yaw: Float): BukkitDirection {
            return by2D[kotlin.math.abs((ifloor(yaw / 90.0 + 0.5) and 3) % by2D.size)]
        }
    }
}