package ink.ptms.adyeshach.impl.util

import ink.ptms.adyeshach.core.util.BukkitColor
import ink.ptms.adyeshach.core.util.JavaColor
import taboolib.common5.cint

inline fun Boolean.ifTrue(func: () -> Unit) {
    if (this) func()
}

fun String.toRGB(): BukkitColor {
    val color = split(",")
    val r = color.getOrNull(0).cint
    val g = color.getOrNull(1).cint
    val b = color.getOrNull(2).cint
    return BukkitColor.fromRGB(r, g, b)
}

fun String.toRGBA(): JavaColor {
    val color = split(",")
    val r = color.getOrNull(0).cint
    val g = color.getOrNull(1).cint
    val b = color.getOrNull(2).cint
    val a = color.getOrElse(3) { 255 }.cint
    return JavaColor(r, g, b, a)
}