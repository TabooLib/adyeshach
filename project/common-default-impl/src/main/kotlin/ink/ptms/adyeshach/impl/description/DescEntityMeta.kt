package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.common.util.errorBy
import taboolib.common.platform.function.info
import java.io.InputStream

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.description.DescEntityMeta
 *
 * @author 坏黑
 * @since 2022/6/20 21:54
 */
class DescEntityMeta(input: InputStream) : Description(input) {

    override val name: String = "entity_meta.desc"

    override fun load(part: DescriptionBlock) {
        val namespace = part.next()
        val currentMeta = ArrayList<PrepareMeta>()

        fun apply() {
            info(namespace)
            info("  apply meta $currentMeta")
        }

        while (part.hasNext()) {
            val line = part.next()
            when {
                line.startsWith(" ".repeat(8)) -> {

                }
                line.startsWith(" ".repeat(4)) -> {
                    if (currentMeta.isNotEmpty()) {
                        apply()
                        currentMeta.clear()
                    }
                    currentMeta += line.trim().split("|").map { parseMeta(it.trim()) }
                }
            }
        }

        apply()
    }

    private fun parseMeta(input: String): PrepareMeta {
        val args = input.split(" ")
        val metaType = parseMetaType(args[1])
        return metaType.parse(args[0], args.subList(1, args.size))
    }

    private fun parseMetaType(value: String): PrepareMetaType {
        return when {
            // integer
            value == "i" -> PrepareMetaTypeGeneric(GenericType.INT)
            // float
            value == "f" -> PrepareMetaTypeGeneric(GenericType.FLOAT)
            // boolean
            value == "z" -> PrepareMetaTypeGeneric(GenericType.BOOLEAN)
            // string
            value == "t" -> PrepareMetaTypeGeneric(GenericType.STRING)
            // byte
            value == "b" -> PrepareMetaTypeGeneric(GenericType.BYTE)
            // byte masked
            value.endsWith('b') -> PrepareMetaTypeGeneric(GenericType.BYTE_MASKED)
            // custom
            value.startsWith('!') -> {
                PrepareMetaTypeCustom(CustomType.values().firstOrNull { it.id == value.substring(1) } ?: errorBy("error-unknown-meta-type", value))
            }
            else -> errorBy("error-unknown-meta-type", value)
        }
    }
}