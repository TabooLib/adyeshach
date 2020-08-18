package ink.ptms.adyeshach.common.bukkit

/**
 * @Author sky
 * @Since 2020-08-17 17:28
 */
enum class BukkitOcelotType {

    UNTAMED,
    TUXEDO,
    TABBY,
    SIAMESE;

    companion object {

        fun of(index: Int): BukkitOcelotType {
            return values()[index]
        }
    }
}