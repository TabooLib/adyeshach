package ink.ptms.adyeshach.common.bukkit

/**
 * @author Arasple
 * @date 2020/8/6 17:49
 */
@Deprecated("Outdated but usable")
enum class BukkitDirection {

    DOWN,
    UP,
    NORTH,
    SOUTH,
    WEST,
    EAST;

    fun v2(): ink.ptms.adyeshach.core.bukkit.BukkitDirection {
        return ink.ptms.adyeshach.core.bukkit.BukkitDirection.values()[ordinal]
    }

    companion object {

        fun of(index: Int): BukkitDirection {
            return values()[index]
        }
    }
}