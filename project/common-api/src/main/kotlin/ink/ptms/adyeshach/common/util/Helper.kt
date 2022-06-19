package ink.ptms.adyeshach.common.util

import com.google.common.base.Enums
import org.bukkit.Location

interface Function2<T1, T2, R> {

    fun invoke(arg1: T1, arg2: T2): R
}

fun Location.safeDistance(loc: Location): Double {
    return if (world!!.name == loc.world!!.name) distance(loc) else Double.MAX_VALUE
}

fun <T : Enum<T>> Class<T>.getEnum(vararg name: String): T {
    name.forEach { Enums.getIfPresent(this, it).orNull()?.let { e -> return e } }
    error("Unable to find enum ${name.joinToString(" ")} from $simpleName")
}

fun <T : Enum<T>> Class<T>.getEnumOrNull(vararg name: String): T? {
    name.forEach { Enums.getIfPresent(this, it).orNull()?.let { e -> return e } }
    return null
}