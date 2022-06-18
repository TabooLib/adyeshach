package ink.ptms.adyeshach.internal.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.api.AdyeshachNetworkAPI
import org.bukkit.command.CommandSender
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.warning
import java.io.*
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

/**
 * @author sky, libraryaddict
 * @since 2020-8-4 23:43:56
 */
@Suppress("SpellCheckingInspection")
class NetworkMineskin : AdyeshachNetworkAPI.Skin {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create()
    private val crlf: String
        get() = "\r\n"

    override fun getTexture(name: String): AdyeshachNetworkAPI.SkinTexture? {
        val file = File(getDataFolder(), "skin/$name")
        if (file.exists() && file.length() > 1) {
            val json = JsonParser().parse(file.readText(StandardCharsets.UTF_8)).asJsonObject
            if (json.size() == 0) {
                warning("Unable to read valid data for $name from $file, please delete it.")
                return null
            }
            return when {
                json.has("network") -> Texture(json.get("value").asString, json.get("signature").asString)
                else -> {
                    try {
                        // legacy version
                        val texture = json.getAsJsonObject("members").getAsJsonObject("data").getAsJsonObject("members").getAsJsonObject("texture")
                            .getAsJsonObject("members")
                        Texture(texture.getAsJsonObject("value").get("value").asString, texture.getAsJsonObject("signature").get("value").asString)
                    } catch (ignored: NullPointerException) {
                        // new version 2021/9/19
                        val texture = json.getAsJsonObject("data").getAsJsonObject("texture")
                        Texture(texture["value"].asString, texture["signature"].asString)
                    }
                }
            }
        } else {
            val ashcon = Adyeshach.api().getNetworkAPI().getAshcon()
            val json = ashcon.getProfile(name)
            if (json == null || json.size() == 0) {
                warning("Unable to request valid data for $name from AshconAPI")
                return null
            }
            return if (json.has("uuid")) {
                val texture = Texture(ashcon.getTextureValue(name)!!, ashcon.getTextureSignature(name)!!)
                newFile(file).writeText(gson.toJson(texture))
                texture
            } else {
                null
            }
        }
    }

    override fun uploadTexture(file: File, model: AdyeshachNetworkAPI.SkinModel, sender: CommandSender): JsonObject? {
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
                                val error = Gson().fromJson(
                                    BufferedReader(InputStreamReader(connection.errorStream, StandardCharsets.UTF_8)).readText(), Error::class.java
                                )
                                when (error.code) {
                                    403 -> {
                                        sender.sendMessage("§c[Adyeshach] §7mineskin.org denied access to that url")
                                    }

                                    404 -> {
                                        sender.sendMessage("§c[Adyeshach] §7mineskin.org unable to find an image at that url")
                                    }

                                    408, 504, 599 -> {
                                        sender.sendMessage("§c[Adyeshach] §7Took too long to connect to mineskin.org!")
                                    }

                                    else -> {
                                        sender.sendMessage("§c[Adyeshach] §7mineskin.org took too long to connect! Is your image valid?")
                                    }
                                }
                            }

                            400 -> {
                                sender.sendMessage("§c[Adyeshach] §7Invalid file provided! Please ensure it is a valid .png skin!")
                            }

                            else -> {
                                connection.inputStream.use { input ->
                                    return JsonParser().parse(
                                        BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"))
                                    ).asJsonObject
                                }
                            }
                        }
                    }
                }
            }
        } catch (t: SocketTimeoutException) {
            sender.sendMessage("§c[Adyeshach] §7Took too long to connect to mineskin.org!")
        } catch (t: Throwable) {
            sender.sendMessage("§c[Adyeshach] §7Unexpected error while accessing mineskin.org, please try again.")
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
}