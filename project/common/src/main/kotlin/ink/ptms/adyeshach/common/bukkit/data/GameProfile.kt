package ink.ptms.adyeshach.common.bukkit.data

import com.google.gson.annotations.Expose

/**
 * @author sky
 * @since 2020-08-05 18:31
 */
class GameProfile {

    @Expose
    var name = "Adyeshach"

    /**
     * A ping that negative (i.e. not known to the server yet) will result in the no connection icon.
     * A ping under 150 milliseconds will result in 5 bars
     * A ping under 300 milliseconds will result in 4 bars
     * A ping under 600 milliseconds will result in 3 bars
     * A ping under 1000 milliseconds (1 second) will result in 2 bars
     * A ping greater than or equal to 1 second will result in 1 bar.
     */
    @Expose
    var ping = 60

    @Expose
    var texture = arrayOf("")

    @Expose
    var textureName = ""

    fun setPingBar(pingBar: PingBar) {
        ping = pingBar.value
    }

    fun clone(): GameProfile {
        val gameProfile = GameProfile()
        gameProfile.name = name
        gameProfile.ping = ping
        gameProfile.texture = texture.clone()
        gameProfile.textureName = textureName
        return gameProfile
    }
}