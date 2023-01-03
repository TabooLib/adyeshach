package ink.ptms.adyeshach.common.bukkit.data

/**
 * @author sky
 * @since 2020-08-05 18:31
 */
@Deprecated("Outdated but usable")
class GameProfile(val v2: ink.ptms.adyeshach.core.bukkit.data.GameProfile) {

    var name: String
        get() = v2.name
        set(value) {
            v2.name = value
        }

    var ping: Int
        get() = v2.ping
        set(value) {
            v2.ping = value
        }

    var texture: Array<String>
        get() = v2.texture
        set(value) {
            v2.texture = value
        }

    var textureName: String
        get() = v2.textureName
        set(value) {
            v2.textureName = value
        }

    fun clone(): GameProfile {
        return GameProfile(v2.clone())
    }
}