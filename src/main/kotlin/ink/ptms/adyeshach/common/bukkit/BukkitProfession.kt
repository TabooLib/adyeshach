package ink.ptms.adyeshach.common.bukkit

/**
 * @Author sky
 * @Since 2020-08-05 0:36
 */
enum class BukkitProfession {

    FARMER,
    LIBRARIAN,
    PRIEST,
    BLACK_SMITH,
    BUTCHER,
    NITWIT;

    companion object {

        fun of(index: Int): BukkitProfession {
            return values()[index]
        }
    }
}