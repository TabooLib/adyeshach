package ink.ptms.adyeshach.core.bukkit

import ink.ptms.adyeshach.core.util.ifloor

/**
 * @author Arasple
 * @date 2020/8/6 17:49
 */
enum class BukkitDirection(val legacyDirection:Int,val direction: Int) {

    DOWN(-1,-1),

    UP(-1,-1),

    NORTH(2,2),

    SOUTH(0,3),

    WEST(1,4),

    EAST(3,5);

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