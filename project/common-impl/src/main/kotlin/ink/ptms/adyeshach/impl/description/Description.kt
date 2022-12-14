package ink.ptms.adyeshach.impl.description

import java.io.InputStream
import java.nio.charset.StandardCharsets

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.description.Description
 *
 * @author 坏黑
 * @since 2022/6/20 21:54
 */
abstract class Description(val input: InputStream) {

    abstract val name: String

    abstract fun load(part: DescriptionBlock)

    open fun loaded() {
    }

    open fun init() {
        try {
            val lines = input.readBytes().toString(StandardCharsets.UTF_8).lines().filter { !it.trim().startsWith('#') }
            val part = ArrayList<String>()
            var i = 0
            while (i < lines.size) {
                val line = lines[i++]
                if (line.isBlank()) {
                    if (part.any { it.isNotBlank() }) {
                        load(DescriptionBlock(part, i))
                        part.clear()
                    }
                } else {
                    part += line
                }
            }
            if (part.isNotEmpty()) {
                load(DescriptionBlock(part, i))
            }
            loaded()
        } catch (ex: Throwable) {
            throw IllegalStateException("Unable to load description file: $name", ex)
        }
    }
}