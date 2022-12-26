package ink.ptms.adyeshach.impl.network

import ink.ptms.adyeshach.core.AdyeshachNetworkAPI
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

/**
 * @author Arasple
 * @date 2020/8/4 20:38
 */
class NetworkAshcon : AdyeshachNetworkAPI.Ashcon {

    val ashconURL = "https://api.ashcon.app/mojang/v2/user/"

    override fun getTexture(name: String): CompletableFuture<AdyeshachNetworkAPI.SkinTexture> {
        val future = CompletableFuture<AdyeshachNetworkAPI.SkinTexture>()
        submitAsync {
            val section = Configuration.loadFromString(readFromURL("$ashconURL$name"), Type.JSON)
            if (section.contains("uuid")) {
                future.complete(NetworkMineskin.Texture(section.getString("textures.raw.value")!!, section.getString("textures.raw.signature")!!))
            } else {
                warning("Unable to request valid data for $name from AshconAPI: ${section.getString("reason")}")
            }
        }
        return future
    }

    fun readFromURL(url: String): String {
        return URL(url).openStream().readBytes().toString(StandardCharsets.UTF_8)
    }
}