package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.api.Adyeshach
import taboolib.common.platform.function.console
import taboolib.module.nms.MinecraftVersion

val minecraftVersion: Int
    get() = MinecraftVersion.majorLegacy

fun assert(condition: Boolean, metadata: String) {
    if (condition) errorBy("error-metadata-not-supported", metadata)
}

fun assert(condition: Boolean, metadata: String, replace: String) {
    if (condition) errorBy("error-metadata-not-supported-use-instead", metadata, replace)
}

fun errorBy(node: String, vararg args: Any): Nothing = error(Adyeshach.api().getLanguage().getLang(console().cast(), node, *args) ?: node)