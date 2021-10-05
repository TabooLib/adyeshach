package ink.ptms.adyeshach.common.bukkit

/**
 * @author sky
 * @since 2020-08-05 0:36
 */
enum class BukkitMushroom {

    RED,
    BROWN;

    companion object {

        fun of(index: Int): BukkitMushroom {
            return values()[index]
        }
    }
}