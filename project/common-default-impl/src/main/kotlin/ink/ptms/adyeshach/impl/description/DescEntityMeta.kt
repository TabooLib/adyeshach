package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.util.errorBy
import taboolib.common.platform.function.info
import taboolib.module.nms.MinecraftVersion
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

    var count = 0
    val major = MinecraftVersion.major

    override fun load(part: DescriptionBlock) {
        val namespace = part.next()
        val adyeshachInterface: Class<*> = kotlin.runCatching { Class.forName(namespace) }.getOrNull() ?: return
        var support = true
        val currentMeta = ArrayList<PrepareMeta>()

        var i = 0
        val index = ArrayList<Pair<Int, Int>>()
        val indexLast = ArrayList<Pair<Int, Int>>()

        fun apply() {
            if (support) {
                currentMeta.forEach { meta ->
                    count++
                    meta.register(adyeshachInterface, index.firstOrNull { major >= toMajor(it.first) }?.second ?: -1)
                }
            }
            support = true
            currentMeta.clear()
            i = 0
            indexLast.clear()
            indexLast.addAll(index)
            index.clear()
        }

        while (part.hasNext()) {
            val line = part.next()
            when {
                line.startsWith(" ".repeat(8)) -> {
                    val idx = line.trim()
                    // 版本限制
                    if (idx.startsWith('@')) {
                        val major = toMajor(idx.substring(1, idx.length - 1).toInt())
                        when {
                            idx.endsWith('+') -> support = MinecraftVersion.major >= major
                            idx.endsWith('-') -> support = MinecraftVersion.major < major
                        }
                    } else {
                        val args = idx.split(" ")
                        if (args[0] == "~") {
                            index.addAll(indexLast.map { it.first to it.second + 1 })
                        } else if (args.size == 2) {
                            when {
                                // 禁用
                                args[1] == "!" -> {
                                    index.add(args[0].toInt() to -1)
                                }
                                // 减少
                                args[1].startsWith('-') -> {
                                    index.add(args[0].toInt() to index[i - 1].second - args[1].count { it == '-' })
                                }
                                // 不变
                                args[1] == "~" -> {
                                    index.add(args[0].toInt() to index[i - 1].second)
                                }
                                // 政策
                                else -> {
                                    index.add(args[0].toInt() to args[1].toInt())
                                }
                            }
                        } else if (args.size == 1) {
                            index.add(0 to args[0].toInt())
                        } else {
                            errorBy("error-meta-index-invalid", idx, namespace)
                        }
                        i++
                    }
                }
                line.startsWith(" ".repeat(4)) -> {
                    if (currentMeta.isNotEmpty()) {
                        apply()
                    }
                    currentMeta += line.trim().split("|").map { parseMeta(it.trim()) }
                }
            }
        }
        apply()
    }

    override fun loaded() {
        info("Load $count entity metadata from the \"$name\"")
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

    private fun toMajor(version: Int): Int {
        return version - 8
    }
}