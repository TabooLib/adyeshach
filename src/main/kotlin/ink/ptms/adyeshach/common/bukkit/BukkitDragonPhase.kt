package ink.ptms.adyeshach.common.bukkit

/**
 * @author sky
 * @since 2020-08-05 0:36
 */
enum class BukkitDragonPhase {

    CIRCLING,
    STRAFING,
    FLYING_TO_THE_PORTAL_TO_LAND,
    LANDING_ON_THE_PORTAL,
    TAKING_OFF_FROM_THE_PORTAL,
    LANDED_PREFORMING_BREATH_ATTACK,
    LANDED_LOOKING_FOR_A_PLAYER_FOR_BREATH_ATTACK,
    LANDED_ROAR_BEFORE_BEGINNING_BREATH_ATTACK,
    CHARGING_PLAYER,
    FLYING_TO_PORTAL_TO_DIE,
    HOVERING_WITH_NO_AI;

    companion object {

        fun of(index: Int): BukkitDragonPhase {
            return values()[index]
        }
    }
}