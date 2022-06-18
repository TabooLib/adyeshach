package ink.ptms.adyeshach.common.util

import org.bukkit.Location

interface Function2<T1, T2, R> {

    fun invoke(arg1: T1, arg2: T2): R
}

fun Location.safeDistance(loc: Location): Double {
    return if (world!!.name == loc.world!!.name) distance(loc) else Double.MAX_VALUE
}