package ink.ptms.adyeshach.common.bukkit

/**
 * @author sky
 * @since 2020-08-05 0:36
 */
enum class BukkitCreeperState(val value: Int) {

    IDLE(-1),
    FUSE(1);

    companion object {

        fun of(index: Int): BukkitCreeperState {
            return values()[index]
        }
    }
}