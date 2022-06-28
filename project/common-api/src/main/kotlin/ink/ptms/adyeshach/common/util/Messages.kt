package ink.ptms.adyeshach.common.util

import ink.ptms.adyeshach.common.api.Adyeshach
import taboolib.common.platform.function.console

fun asLang(node: String, vararg args: Any) = Adyeshach.api().getLanguage().getLang(console().cast(), node, *args) ?: node

fun errorBy(node: String, vararg args: Any): Nothing = error(Adyeshach.api().getLanguage().getLang(console().cast(), node, *args) ?: node)