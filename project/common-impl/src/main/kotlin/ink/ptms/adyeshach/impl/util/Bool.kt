package ink.ptms.adyeshach.impl.util

inline fun Boolean.ifTrue(func: () -> Unit) {
    if (this) func()
}