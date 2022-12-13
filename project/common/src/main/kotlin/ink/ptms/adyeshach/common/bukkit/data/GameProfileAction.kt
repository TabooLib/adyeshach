package ink.ptms.adyeshach.common.bukkit.data

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.bukkit.data.GameProfileAction
 *
 * @author 坏黑
 * @since 2022/12/13 11:18
 */
enum class GameProfileAction {

    ADD_PLAYER, UPDATE_GAME_MODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, LEGACY_REMOVE_PLAYER;

    companion object {

        fun addAction11903() = listOf(ADD_PLAYER, UPDATE_GAME_MODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, UPDATE_LISTED)

        fun addAction11900() = listOf(ADD_PLAYER, UPDATE_GAME_MODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME)
    }
}