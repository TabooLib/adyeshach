package ink.ptms.adyeshach.common.path

import org.bukkit.Location

class PathSchedule(val start: Location, val target: Location, val pathType: PathType, val call: (PathResult) -> (Unit)) {

    val beginTime = System.currentTimeMillis()
}