package ink.ptms.adyeshach.common.util

import org.bukkit.Location

fun Location.safeDistance(loc: Location): Double {
    return if (world!!.name == loc.world!!.name) distance(loc) else Double.MAX_VALUE
}