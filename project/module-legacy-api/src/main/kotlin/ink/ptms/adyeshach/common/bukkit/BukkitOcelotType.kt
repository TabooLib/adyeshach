package ink.ptms.adyeshach.common.bukkit

/**
 * @author sky
 * @since 2020-08-17 17:28
 */
@Deprecated("Outdated but usable")
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