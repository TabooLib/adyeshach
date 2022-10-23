package ink.ptms.adyeshach.api

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author Arasple
 * @date 2020/8/4 20:38
 */
object AshconAPI {

    private val url = arrayOf("https://api.ashcon.app/mojang/v2/user/")
    private val profileCache = Collections.synchronizedMap(HashMap<String, JsonObject>())

    fun getTextureValue(name: String): String {
        return getProfile(name).getAsJsonObject("textures").getAsJsonObject("raw").get("value").asString
    }

    fun getTextureSignature(name: String): String {
        return getProfile(name).getAsJsonObject("textures").getAsJsonObject("raw").get("signature").asString
    }

    fun getProfile(name: String): JsonObject {
        return profileCache.getOrDefault(name, JsonObject())
    }

    fun readFromURL(url: String): String {
        return URL(url).openStream().readBytes().toString(StandardCharsets.UTF_8)
    }

    @SubscribeEvent
    private fun onJoin(e: PlayerJoinEvent) {
        if (AdyeshachSettings.ashconAPI) {
            submit(async = true) {
                try {
                    profileCache[e.player.name] = JsonParser().parse(readFromURL("${url[0]}${e.player.name}")).asJsonObject
                } catch (ignore: FileNotFoundException) {
                } catch (ignore: NullPointerException) {
                } catch (ignore: IOException) {
                }
            }
        }
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        profileCache.remove(e.player.name)
    }
}