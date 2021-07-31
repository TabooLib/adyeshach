package ink.ptms.adyeshach.common.entity.path

import ink.ptms.adyeshach.api.AdyeshachSettings
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common.platform.submit
import taboolib.common5.mirrorNow
import taboolib.module.navigation.*
import taboolib.module.nms.MinecraftVersion

/**
 * @Author sky
 * @Since 2020-08-13 16:31
 */
object PathFinderHandler {

    val version = MinecraftVersion.majorLegacy

    fun request(start: Location, target: Location, pathType: PathType = PathType.WALK_2, request: Request = Request.NAVIGATION, call: (Result) -> (Unit)) {
        if (start.world!!.name != target.world!!.name) {
            error("cannot request navigation in different worlds.")
        }
        submit(async = !AdyeshachSettings.pathfinderSync) {
            val startTime = System.currentTimeMillis()
            if (request == Request.NAVIGATION) {
                mirrorNow("PathFinderProxy:Native:Navigation") {
                    val time = System.currentTimeMillis()
                    val pathFinder = createPathfinder(NodeEntity(start, pathType.height, pathType.width))
                    val path = pathFinder.findPath(target, distance = 32f)
                    if (AdyeshachSettings.debug) {
                        path?.nodes?.forEach { it.display(target.world!!) }
                    }
                    call(ResultNavigation(path?.nodes?.map { it.asBlockPos() } ?: emptyList(), startTime, time))
                }
            } else {
                mirrorNow("PathFinderProxy:Native:RandomPosition") {
                    val time = System.currentTimeMillis()
                    var vec: Vector? = null
                    repeat(10) {
                        if (vec == null) {
                            vec = RandomPositionGenerator.generateLand(NodeEntity(start, pathType.height, pathType.width), 10, 7)
                        }
                    }
                    if (vec != null) {
                        call(ResultRandomPosition(vec, startTime, time))
                    }
                }
            }
        }
    }
}