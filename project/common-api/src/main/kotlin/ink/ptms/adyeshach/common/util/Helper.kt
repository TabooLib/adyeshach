package ink.ptms.adyeshach.common.util

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.api.Adyeshach
import org.bukkit.Location
import taboolib.common.platform.function.console
import taboolib.module.lang.asLangText

interface Function2<T1, T2, R> {

    fun invoke(arg1: T1, arg2: T2): R
}

fun Location.safeDistance(loc: Location): Double {
    return if (world!!.name == loc.world!!.name) distance(loc) else Double.MAX_VALUE
}

fun <T : Enum<T>> Class<T>.getEnum(vararg name: String): T {
    name.forEach { Enums.getIfPresent(this, it).orNull()?.let { e -> return e } }
    errorBy("error-unable-to-find-enum", simpleName, name.joinToString(" "))
}

fun <T : Enum<T>> Class<T>.getEnumOrNull(vararg name: String): T? {
    name.forEach { Enums.getIfPresent(this, it).orNull()?.let { e -> return e } }
    return null
}

fun String?.toReadable(): String {
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

fun asLang(node: String, vararg args: Any) = Adyeshach.api().getLanguage().getLang(console().cast(), node, *args) ?: node

fun errorBy(node: String, vararg args: Any): Nothing = error(Adyeshach.api().getLanguage().getLang(console().cast(), node, *args) ?: node)