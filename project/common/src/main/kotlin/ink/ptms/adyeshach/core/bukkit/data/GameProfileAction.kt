package ink.ptms.adyeshach.core.bukkit.data

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.bukkit.data.GameProfileAction
 *
 * @author 坏黑
 * @since 2022/12/13 11:18
 */
enum class GameProfileAction {

    ADD_PLAYER, UPDATE_GAME_MODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, LEGACY_REMOVE_PLAYER;

    companion object {

        fun initActions() = listOf(ADD_PLAYER, UPDATE_GAME_MODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, UPDATE_LISTED)
    }
}