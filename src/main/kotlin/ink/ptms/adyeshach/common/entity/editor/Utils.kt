@file:Suppress("UNCHECKED_CAST")

package ink.ptms.adyeshach.common.entity.editor

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Meta
import org.bukkit.entity.Player
import taboolib.module.chat.colored
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText

internal val minecraftVersion = MinecraftVersion.majorLegacy
internal val cacheEnums = HashMap<String, Array<out Any>>()

internal fun Class<*>.enums(): Array<out Any> {
    return cacheEnums.computeIfAbsent(name) { enumConstants }
}

internal fun <T> at(vararg index: Pair<Int, T>): T {
    return (index.firstOrNull { minecraftVersion >= it.first }?.second ?: -1) as T
}

internal fun EntityInstance.forEachMeta(func: (Meta, Boolean) -> Unit) {
    getEditableEntityMeta().forEach { func.invoke(it, HideMetas.isHideMeta(this, it)) }
}

internal fun String.minimize(): String {
    return if (length > 16) substring(0, length - (length - 10)) + "..." + substring(length - 7) else this
}

internal fun Boolean?.toDisplay(player: Player): String {
    return if (this == true) player.asLangText("editor-meta-true") else player.asLangText("editor-meta-false")
}

internal fun String?.toDisplay(): String {
    val builder = StringBuilder()
    toString().toCharArray().forEachIndexed { index, c ->
        when {
            index == 0 -> builder.append(c.uppercaseChar())
            c.isUpperCase() -> builder.append(" $c")
            else -> builder.append(c)
        }
    }
    return builder.toString()
}

internal fun String.toLocale(player: Player): String {
    return player.asLangText("editor-meta-${toLocaleKey()}")
}

internal fun String.toLocaleKey(): String {
    val builder = StringBuilder()
    toString().toCharArray().forEachIndexed { _, c ->
        when {
            c.isUpperCase() -> builder.append("-${c.lowercaseChar()}")
            else -> builder.append(c)
        }
    }
    return builder.toString()
}

internal fun Player.edit(entity: EntityInstance, value: Any, function: (value: String) -> Unit) {
    inputSign(arrayOf("$value", "", asLangText("editor-sign-input"))) { args ->
        if (args[0].isNotEmpty()) {
            function((args[0] + args[1]).colored())
        }
        entity.openEditor(this)
    }
}