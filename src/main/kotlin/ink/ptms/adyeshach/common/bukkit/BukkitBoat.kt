package ink.ptms.adyeshach.common.bukkit

/**
 * @Author sky
 * @Since 2020-08-05 0:36
 */
enum class BukkitBoat {

    OAK,
    SPRUCE,
    BIRCH,
    JUNGLE,
    ACACIA,
    DARK_OAK;

    companion object {

        fun of(index: Int): BukkitBoat {
            return values()[index]
        }
    }
}