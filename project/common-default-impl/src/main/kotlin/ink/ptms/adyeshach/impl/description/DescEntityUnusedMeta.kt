package ink.ptms.adyeshach.impl.description

import taboolib.common.platform.function.info
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.description.DescEntityUnusedMeta
 *
 * @author 坏黑
 * @since 2022/9/6 11:15
 */
class DescEntityUnusedMeta(input: InputStream) : Description(input) {

    val metaMap = ConcurrentHashMap<Class<*>, MutableList<String>>()

    override val name: String = "entity_meta_unused.desc"

    override fun load(part: DescriptionBlock) {
        val interfaces = part.source.filter { !it.startsWith(' ') }.map { Class.forName(it) }
        val meta = part.source.filter { it.startsWith(" ".repeat(4)) }
        interfaces.forEach {
            metaMap.computeIfAbsent(it) { mutableListOf() }.addAll(meta)
        }
    }

    override fun loaded() {
        info("Load $metaMap unused entity metadata from the \"$name\"")
    }
}