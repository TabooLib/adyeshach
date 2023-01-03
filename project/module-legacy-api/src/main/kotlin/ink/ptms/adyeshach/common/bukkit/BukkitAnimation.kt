package ink.ptms.adyeshach.common.bukkit

/**
 * @author sky
 * @since 2020-08-28 1:49
 */
@Deprecated("Outdated but usable")
enum class BukkitAnimation(val id: Int) {

    SWING_MAIN_HAND(0),

    TAKE_DAMAGE(1),

    LEAVE_BED(2),

    SWING_OFFHAND(3),

    CRITICAL_EFFECT(4),

    MAGIC_CRITICAL_EFFECT(5);

    fun v2(): ink.ptms.adyeshach.core.bukkit.BukkitAnimation {
        return ink.ptms.adyeshach.core.bukkit.BukkitAnimation.values()[ordinal]
    }
}