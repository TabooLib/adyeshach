package ink.ptms.adyeshach.common.entity.path

import org.bukkit.Location
import taboolib.module.nms.MinecraftVersion

/**
 * @author sky
 * @since 2020-08-13 16:31
 */
@Deprecated("Outdated but usable")
object PathFinderHandler {

    val version = MinecraftVersion.majorLegacy

    fun request(start: Location, target: Location, pathType: PathType = PathType.WALK_2, request: Request = Request.NAVIGATION, call: (Result) -> (Unit)) {
        return ink.ptms.adyeshach.core.entity.path.PathFinderHandler.request(start, target, pathType.v2(), request.v2()) {
            if (it is ink.ptms.adyeshach.core.entity.path.ResultNavigation) {
                call(ResultNavigation(it.pointList, it.beginTime, it.scheduleTime))
            } else if (it is ink.ptms.adyeshach.core.entity.path.ResultRandomPosition) {
                call(ResultRandomPosition(it.random, it.beginTime, it.scheduleTime))
            }
        }
    }
}