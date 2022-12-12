package ink.ptms.adyeshach.impl.description

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.description.DescriptionBlock
 *
 * @author 坏黑
 * @since 2022/6/21 14:46
 */
class DescriptionBlock(val source: List<String>) {

    var readLine = 0
        private set

    fun hasNext(): Boolean {
        return readLine < source.size
    }

    fun next(): String {
        return source[readLine++]
    }

    fun peek(): String? {
        return source.getOrNull(readLine + 1)
    }

    operator fun get(index: Int): String {
        return source[index]
    }
}