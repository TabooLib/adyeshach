package ink.ptms.adyeshach.internal

import taboolib.module.kether.printKetherErrorMessage

internal fun runKether(func: () -> Unit) {
    try {
        func()
    } catch (ex: Exception) {
        ex.printKetherErrorMessage()
    }
}