package ink.ptms.adyeshach.api

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.Files
import io.izzel.taboolib.util.IO
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.BufferedInputStream
import java.net.URL
import java.net.UnknownHostException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author Arasple
 * @date 2020/8/4 20:38
 */
object AshconAPI {

    private val ASHCON_API = arrayOf("https://api.ashcon.app/mojang/v2/user/")

    @PlayerContainer
    private val CACHED_PROFILES = mutableMapOf<String, JsonObject>()

    fun getTextureValue(name: String): String = getProfile(name).getAsJsonObject("textures").getAsJsonObject("raw").get("value").asString

    fun getTextureSignature(name: String): String = getProfile(name).getAsJsonObject("textures").getAsJsonObject("raw").get("signature").asString

    fun getProfile(name: String) = CACHED_PROFILES.computeIfAbsent(name) {
        JsonParser().parse(readFromURL("${ASHCON_API[0]}$name", StandardCharsets.UTF_8)).asJsonObject
    }

    @TListener
    class ListenerJoin : Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        fun onJoin(e: PlayerJoinEvent) {
            Tasks.task(true) {
                try {
                    getProfile(e.player.name)
                } catch (ignore: NullPointerException) {
                }
            }
        }
    }

    fun readFromURL(url: String, charset: Charset): String {
        try {
            URL(url).openStream().use { inputStream ->
                BufferedInputStream(inputStream).use { bufferedInputStream ->
                    return String(IO.readFully(bufferedInputStream), charset)
                }
            }
        } catch (ignored: Throwable) {
        }
        return "{}"
    }
}