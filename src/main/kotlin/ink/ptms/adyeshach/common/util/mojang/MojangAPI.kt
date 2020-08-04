package ink.ptms.adyeshach.common.util.mojang

import io.izzel.taboolib.internal.gson.Gson
import io.izzel.taboolib.internal.gson.JsonObject
import io.izzel.taboolib.internal.gson.JsonParser
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.io.*
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.stream.Collectors

/**
 * @author sky, libraryaddict
 * @since 2020-8-4 23:43:56
 */
object MojangAPI {

    const val CRLF = "\r\n"

    fun upload(file: File, sender: CommandSender = Bukkit.getConsoleSender()): JsonObject? {
        try {
            val url = URL("https://api.mineskin.org/generate/upload")
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
                        printWriter.append("--").append(boundary).append(CRLF)
                        printWriter.append("Content-Disposition: form-data; name=\"visibility\"").append(CRLF)
                        printWriter.append("Content-Type: text/plain; charset=").append(StandardCharsets.UTF_8.name()).append(CRLF)
                        printWriter.append(CRLF).append("1").append(CRLF).flush()
                        // body
                        printWriter.append("--").append(boundary).append(CRLF)
                        printWriter.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.name).append("\"").append(CRLF)
                        printWriter.append("Content-Type: image/png").append(CRLF)
                        printWriter.append("Content-Transfer-Encoding: binary").append(CRLF)
                        printWriter.append(CRLF).flush()
                        Files.copy(file.toPath(), output)
                        output.flush()
                        printWriter.append(CRLF).flush()
                        // end
                        writer.append("--").append(boundary).append("--").append(CRLF).flush()
                        when (connection.responseCode) {
                            500 -> {
                                val error = Gson().fromJson(BufferedReader(InputStreamReader(connection.errorStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")), Error::class.java)
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
                                    return JsonParser.parseString(BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"))).asJsonObject
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
        }
        return null
    }

    class Error(val code: Int, val error: String)
}