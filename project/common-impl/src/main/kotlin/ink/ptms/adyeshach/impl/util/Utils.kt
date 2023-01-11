package ink.ptms.adyeshach.impl.util

import org.bukkit.Color
import taboolib.common5.cint

inline fun Boolean.ifTrue(func: () -> Unit) {
    if (this) func()
}

fun String.toColor(): Color {
    val color = split(",")
    val r = color.getOrNull(0).cint
    val g = color.getOrNull(1).cint
    val b = color.getOrNull(2).cint
    return Color.fromRGB(r, g, b)
}