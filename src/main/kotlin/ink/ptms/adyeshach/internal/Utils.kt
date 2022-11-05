package ink.ptms.adyeshach.internal

import ink.ptms.adyeshach.common.entity.editor.enums
import taboolib.module.kether.printKetherErrorMessage

internal fun runKether(func: () -> Unit) {
    try {
        func()
    } catch (ex: Exception) {
        ex.printKetherErrorMessage()
    }
}

fun Class<*>.findEnum(name: String): Enum<*> {
    return enums().firstOrNull { e -> e.toString().equals(name, true) } as? Enum<*> ?: error("Unknown enum $name")
}