package ink.ptms.adyeshach.core.bukkit.data

import com.google.gson.annotations.Expose
import com.mojang.authlib.properties.Property
import taboolib.module.nms.MinecraftVersion
import java.util.*

/**
 * @author sky
 * @since 2020-08-05 18:31
 */
class GameProfile {

    @Expose
    var name = "Adyeshach"
        set(value) {
            // 长度限制
            field = (if (value.length > 16) value.take(16) else value)
        }

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

    /**
     * 玩家皮肤
     * 因历史遗留问题，该数组所表示为：[0] = texture，[1] = signature
     */
    @Expose
    var textureName = ""

    @Expose
    var spectator = false

    @Expose
    var listed = false // 从 2.0.0-snapshot-27 版本开始，默认不在列表中显示

    @Expose
    var showHat = true // 是否显示帽子？于 1.21.4 版本开始支持

    fun setPingBar(pingBar: PingBar) {
        ping = pingBar.value
    }

    fun toMojang(uuid: UUID): com.mojang.authlib.GameProfile {
        val mojangProfile = com.mojang.authlib.GameProfile(uuid, name)
        // 如果有皮肤则添加皮肤
        if (texture.size == 2) {
            mojangProfile.properties.put("textures", Property("textures", texture[0], texture[1]))
        }
        return mojangProfile
    }

    fun clone(): GameProfile {
        val gameProfile = GameProfile()
        gameProfile.name = name
        gameProfile.ping = ping
        gameProfile.texture = texture.clone()
        gameProfile.textureName = textureName
        gameProfile.spectator = spectator
        gameProfile.listed = listed
        return gameProfile
    }

    companion object {

        /**
         * 当前版本是否支持 listed 功能
         */
        val isListedSupported by lazy { MinecraftVersion.majorLegacy >= 11903 }
    }
}