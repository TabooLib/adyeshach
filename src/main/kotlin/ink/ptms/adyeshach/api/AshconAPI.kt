package ink.ptms.adyeshach.api

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * @author Arasple
 * @date 2020/8/4 20:38
 */
object AshconAPI {

    private val url = arrayOf("https://api.ashcon.app/mojang/v2/user/")
    private val profileCache = mutableMapOf<String, JsonObject>()

    fun getTextureValue(name: String): String {
        return getProfile(name).getAsJsonObject("textures").getAsJsonObject("raw").get("value").asString
    }

    fun getTextureSignature(name: String): String {
        return getProfile(name).getAsJsonObject("textures").getAsJsonObject("raw").get("signature").asString
    }

    fun getProfile(name: String) = profileCache.computeIfAbsent(name) {
        JsonParser().parse(readFromURL("${url[0]}$name")).asJsonObject
    }

    fun readFromURL(url: String): String {
        try {
            URL(url).openStream().readBytes().toString(StandardCharsets.UTF_8)
        } catch (ignored: Throwable) {
        }
        return "{}"
    }

    @SubscribeEvent
    private fun e(e: PlayerJoinEvent) {
        submit(async = true) {
            try {
                getProfile(e.player.name)
            } catch (ignore: NullPointerException) {
            }
        }
    }

    @SubscribeEvent
    private fun e(e: PlayerQuitEvent) {
        profileCache.remove(e.player.name)
    }
}