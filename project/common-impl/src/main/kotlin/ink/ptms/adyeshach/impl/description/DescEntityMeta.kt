package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.core.entity.type.AdyEntity
import ink.ptms.adyeshach.core.util.errorBy
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
    val majorLegacy = MinecraftVersion.majorLegacy

    @Suppress("UNCHECKED_CAST")
    override fun load(part: DescriptionBlock) {
        val namespace = part.next()
        val adyeshachInterface = kotlin.runCatching { Class.forName(namespace) as Class<AdyEntity> }.getOrNull() ?: return
        var support = true
        var group = ""
        val currentMeta = ArrayList<PrepareMeta>()

        var i = 0
        val index = ArrayList<Pair<Int, Int>>()
        val indexLast = ArrayList<Pair<Int, Int>>()

        fun apply() {
            if (support) {
                currentMeta.forEach { meta ->
                    count++
                    meta.register(adyeshachInterface, index.firstOrNull { (version, _) ->
                        if (version > 1_0000) majorLegacy >= version else major >= toMajor(version)
                    }?.second ?: -1, group)
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
                // 序列行 —— 8 空格
                line.startsWith(" ".repeat(8)) -> {
                    val idx = line.trim()
                    // 组
                    if (idx.startsWith('&')) {
                        group = idx.substring(1)
                    }
                    // 版本限制
                    else if (idx.startsWith('@')) {
                        // 是否细分版本
                        val checkMajorLegacy = idx.startsWith("@!")

                        val version = if (checkMajorLegacy) majorLegacy else major
                        val check = if (checkMajorLegacy) idx.substring(2, idx.length - 1).toInt() else toMajor(idx.substring(1, idx.length - 1).toInt())

                        when {
                            idx.endsWith('+') -> support = version >= check
                            idx.endsWith('-') -> support = version < check
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
                // 名称行 - 4 空格
                // 这一段必须放在后面，因为 8 个空格的序列行也是以 4 个空格开头
                line.startsWith(" ".repeat(4)) -> {
                    if (currentMeta.isNotEmpty()) {
                        apply()
                    }
                    currentMeta += line.trim().split("|").mapNotNull {
                        // 在低版本解析特殊类型 Meta 会出现异常，例如：Cat.Type, Frog.Variant
                        // 在这里直接过滤掉
                        try {
                            parseMeta(it.trim())
                        } catch (_: NoClassDefFoundError) {
                            null
                        }
                    }
                }
            }
        }
        apply()
    }

    override fun loaded() {
        info("Loaded $count entity metadata from the \"$name\"")
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