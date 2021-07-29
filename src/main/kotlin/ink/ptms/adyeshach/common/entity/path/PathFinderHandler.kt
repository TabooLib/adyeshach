package ink.ptms.adyeshach.common.entity.path

import ink.ptms.adyeshach.api.Settings
import org.bukkit.Location
import org.bukkit.entity.Creature
import org.bukkit.util.Vector
import taboolib.common.platform.submit
import taboolib.common5.mirrorFuture
import taboolib.module.navigation.NodeEntity
import taboolib.module.navigation.NodeReader
import taboolib.module.navigation.PathFinder
import taboolib.module.navigation.RandomPositionGenerator
import taboolib.module.nms.MinecraftVersion
import java.util.*
import java.util.concurrent.ConcurrentHashMap

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
        submit(async = !Settings.pathfinderSync) {
            val startTime = System.currentTimeMillis()
            if (request == Request.NAVIGATION) {
                mirrorFuture<Unit>("PathFinderProxy:Native:Navigation") {
                    val time = System.currentTimeMillis()
                    val pathFinder = PathFinder(NodeReader(NodeEntity(start, pathType.height, pathType.width)))
                    val path = pathFinder.findPath(target, distance = 32f)
                    finish(null)
                    if (Settings.debug) {
                        path?.nodes?.forEach { it.display(target.world!!) }
                    }
                    call(ResultNavigation(path?.nodes?.map { it.asBlockPos() } ?: emptyList(), startTime, time))
                }
            } else {
                mirrorFuture<Unit>("PathFinderProxy:Native:RandomPosition") {
                    val time = System.currentTimeMillis()
                    var vec: Vector? = null
                    repeat(10) {
                        if (vec == null) {
                            vec = RandomPositionGenerator.generateLand(NodeEntity(start, pathType.height, pathType.width), 10, 7)
                        }
                    }
                    finish(null)
                    if (vec != null) {
                        call(ResultRandomPosition(vec, startTime, time))
                    }
                }
            }
        }
    }
}