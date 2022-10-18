package ink.ptms.adyeshach.common.util

import org.bukkit.Location

fun Location.safeDistance(loc: Location): Double {
    return if (world!!.name == loc.world!!.name) distance(loc) else Double.MAX_VALUE
}

fun Location.toBlockCenter(): Location {
    return add(1.5, 1.5, 1.5)
}

fun Location.toGroundCenter(): Location {
    return add(1.5, 0.0, 1.5)
}