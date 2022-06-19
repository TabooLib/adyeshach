package ink.ptms.adyeshach.impl.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ink.ptms.adyeshach.common.api.AdyeshachNetworkAPI
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.TabooLibCommon
import taboolib.common.platform.function.registerBukkitListener
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
class NetworkAshcon : AdyeshachNetworkAPI.Ashcon {

    private val ashconUrl = arrayOf("https://api.ashcon.app/mojang/v2/user/")
    private val profileCache = Collections.synchronizedMap(HashMap<String, JsonObject>())

    init {
        TabooLibCommon.postpone(LifeCycle.ENABLE) {
            registerBukkitListener(PlayerJoinEvent::class.java) {
                submit(async = true) {
                    try {
                        profileCache[it.player.name] = JsonParser().parse(readFromURL("${ashconUrl[0]}${it.player.name}")).asJsonObject
                    } catch (ignore: FileNotFoundException) {
                    } catch (ignore: NullPointerException) {
                    } catch (ignore: IOException) {
                    }
                }
            }
            registerBukkitListener(PlayerQuitEvent::class.java) {
                profileCache.remove(it.player.name)
            }
        }
    }

    override fun getTextureValue(name: String): String? {
        return getProfile(name)?.getAsJsonObject("textures")?.getAsJsonObject("raw")?.get("value")?.asString
    }

    override fun getTextureSignature(name: String): String? {
        return getProfile(name)?.getAsJsonObject("textures")?.getAsJsonObject("raw")?.get("signature")?.asString
    }

    override fun getProfile(name: String): JsonObject? {
        return profileCache.getOrDefault(name, null)
    }

    fun readFromURL(url: String): String {
        return URL(url).openStream().readBytes().toString(StandardCharsets.UTF_8)
    }
}