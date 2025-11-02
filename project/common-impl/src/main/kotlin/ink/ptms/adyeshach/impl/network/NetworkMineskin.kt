package ink.ptms.adyeshach.impl.network

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.ADYESHACH_PREFIX
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachNetworkAPI
import ink.ptms.adyeshach.core.serializer.Serializer
import org.bukkit.command.CommandSender
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.getProxyPlayer
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.function.warning
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.*
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

/**
 * @author sky, libraryaddict
 * @since 2020-8-4 23:43:56
 */
@Suppress("SpellCheckingInspection")
class NetworkMineskin : AdyeshachNetworkAPI.Skin {

    val crlf: String
        get() = "\r\n"

    override fun hasTexture(name: String): Boolean {
        val file = File(getDataFolder(), "skin/$name")
        return file.exists() && file.length() > 1
    }

    override fun getTexture(name: String): CompletableFuture<AdyeshachNetworkAPI.SkinTexture> {
        val future = CompletableFuture<AdyeshachNetworkAPI.SkinTexture>()
        var file = File(getDataFolder(), "skin/$name")
        // 从 ashcon 中检索
        if (!file.exists()) {
            // 开启 ashcon 缓存时，从 ashcon 中检索
            if (enableAshcon) {
                file = File(getDataFolder(), "skin/ashcon/$name")
            }
        }
        if (file.exists() && file.length() > 1) {
            val json = Configuration.loadFromFile(file, Type.JSON)
            if (json.getKeys(false).isEmpty()) {
                warning("Unable to read valid data for $name from $file, please delete it.")
                return future
            }
            future.complete(
                when {
                    // ashcon 缓存
                    json.contains("network") -> Texture(json.getString("value")!!, json.getString("signature")!!)
                    // mineskin 上传
                    else -> {
                        try {
                            // legacy version
                            val texture = json.getConfigurationSection("members.data.members.texture.members")!!
                            Texture(texture.getString("value.value")!!, texture.getString("signature.value")!!)
                        } catch (_: NullPointerException) {
                            // new version 2021/9/19
                            val texture = json.getConfigurationSection("data.texture")!!
                            Texture(texture.getString("value")!!, texture.getString("signature")!!)
                        }
                    }
                }
            )
        }
        // 开启 ashcon 缓存时，请求 ashcon 接口
        else if (enableAshcon) {
            var playerName = name.substringAfterLast('/')
            if (getProxyPlayer(playerName) != null) {
                playerName = getProxyPlayer(playerName)!!.uniqueId.toString()
            }
            Adyeshach.api().getNetworkAPI().getAshcon().getTexture(playerName).thenAccept {
                future.complete(it)
                submitAsync { newFile(getDataFolder(), "skin/ashcon/$name").writeText(Serializer.gson.toJson(it)) }
            }.exceptionally { t ->
                future.completeExceptionally(t)
                null
            }
        } else {
            future.completeExceptionally(IllegalStateException("No skin found for $name"))
        }
        return future
    }

    override fun uploadTexture(file: File, model: AdyeshachNetworkAPI.SkinModel, sender: CommandSender): ConfigurationSection? {
        try {
            val url = URL("https://api.mineskin.org/generate/upload?&model=${model.namespace}")
            val boundary = java.lang.Long.toHexString(System.currentTimeMillis())
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Adyeshach")
            connection.connectTimeout = 30000
            connection.readTimeout = 30000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            connection.outputStream.use { output ->
                OutputStreamWriter(output, StandardCharsets.UTF_8).use { writer ->
                    PrintWriter(writer, true).use { printWriter ->
                        // head
                        printWriter.append("--").append(boundary).append(crlf)
                        printWriter.append("Content-Disposition: form-data; name=\"visibility\"").append(crlf)
                        printWriter.append("Content-Type: text/plain; charset=").append(StandardCharsets.UTF_8.name()).append(crlf)
                        printWriter.append(crlf).append("1").append(crlf).flush()
                        // body
                        printWriter.append("--").append(boundary).append(crlf)
                        printWriter.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.name).append("\"").append(crlf)
                        printWriter.append("Content-Type: image/png").append(crlf)
                        printWriter.append("Content-Transfer-Encoding: binary").append(crlf)
                        printWriter.append(crlf).flush()
                        java.nio.file.Files.copy(file.toPath(), output)
                        output.flush()
                        printWriter.append(crlf).flush()
                        // end
                        writer.append("--").append(boundary).append("--").append(crlf).flush()
                        when (connection.responseCode) {
                            500 -> {
                                val reader = BufferedReader(InputStreamReader(connection.errorStream, StandardCharsets.UTF_8))
                                val error = Gson().fromJson(reader.readText(), Error::class.java)
                                when (error.code) {
                                    403 -> {
                                        sender.sendMessage("${ADYESHACH_PREFIX}mineskin.org denied access to that url")
                                    }

                                    404 -> {
                                        sender.sendMessage("${ADYESHACH_PREFIX}mineskin.org unable to find an image at that url")
                                    }

                                    408, 504, 599 -> {
                                        sender.sendMessage("${ADYESHACH_PREFIX}Took too long to connect to mineskin.org!")
                                    }

                                    else -> {
                                        sender.sendMessage("${ADYESHACH_PREFIX}mineskin.org took too long to connect! Is your image valid?")
                                    }
                                }
                            }

                            400 -> {
                                sender.sendMessage("${ADYESHACH_PREFIX}Invalid file provided! Please ensure it is a valid .png skin!")
                            }

                            else -> {
                                connection.inputStream.use { input ->
                                    val reader = BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8))
                                    return Configuration.loadFromString(reader.readLines().joinToString("\n"), Type.JSON)
                                }
                            }
                        }
                    }
                }
            }
        } catch (t: SocketTimeoutException) {
            sender.sendMessage("${ADYESHACH_PREFIX}Took too long to connect to mineskin.org!")
        } catch (t: Throwable) {
            sender.sendMessage("${ADYESHACH_PREFIX}Unexpected error while accessing mineskin.org, please try again.")
            t.printStackTrace()
        }
        return null
    }

    /**
     * 错误信息
     */
    class Error(val code: Int, val error: String)

    /**
     * 皮肤信息
     */
    class Texture(@Expose val value: String, @Expose val signature: String, @Expose val network: Boolean = true) : AdyeshachNetworkAPI.SkinTexture {

        override fun isNetwork(): Boolean {
            return network
        }

        override fun signature(): String {
            return signature
        }

        override fun value(): String {
            return value
        }
    }

    companion object {

        var enableAshcon = true
    }
}