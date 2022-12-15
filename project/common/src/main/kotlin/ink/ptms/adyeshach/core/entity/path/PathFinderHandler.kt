package ink.ptms.adyeshach.core.entity.path

import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.entity.type.errorBy
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit
import taboolib.module.navigation.NodeEntity
import taboolib.module.navigation.RandomPositionGenerator
import taboolib.module.navigation.createPathfinder

/**
 * @author sky
 * @since 2020-08-13 16:31
 */
object PathFinderHandler {

    /**
     * 请求寻路路径
     *
     * @param start 起点
     * @param target 终点
     * @param pathType 路径类型
     * @param request 请求方式
     * @param call 回调函数
     */
    fun request(start: Location, target: Location, pathType: PathType = PathType.WALK_2, request: Request = Request.NAVIGATION, call: (Result) -> (Unit)) {
        // 世界判断
        if (start.world!!.name != target.world!!.name) {
            errorBy("error-different-worlds")
        }
        // 请求发起时间
        val startTime = System.currentTimeMillis()
        // 是否同步执行代码
        submit(async = !AdyeshachSettings.pathfinderSync) {
            // 请求开始时间
            val scheduleTime = System.currentTimeMillis()
            // 寻路请求
            if (request == Request.NAVIGATION) {
                val pathFinder = createPathfinder(NodeEntity(start, pathType.height, pathType.width))
                // 最大 32 格的寻路请求
                val path = pathFinder.findPath(target, distance = 32f)
                // 调试模式下将显示路径节点
                if (AdyeshachSettings.debug) {
                    path?.nodes?.forEach { it.display(target.world!!) }
                }
                val pointList = path?.nodes?.map { it.asBlockPos() }?.toMutableList() ?: ArrayList()
                if (pointList.isNotEmpty()) {
                    // 如果路径的第一个点是起点则移除
                    val first = pointList.first()
                    if (first.blockX == start.blockX && first.blockX == start.blockY && first.blockX == start.blockZ) {
                        pointList.removeFirst()
                    }
                    // 如果路径的最后一个点不是目的地
                    val last = pointList.last()
                    if (last.blockX != target.blockX || last.blockZ != target.blockZ) {
                        // 如果高度相同，距离为 1
                        if (last.blockY == target.blockY && last.distance(Vector(target.blockX, target.blockY, target.blockZ)) == 1.0) {
                            // 添加目的地
                            pointList.add(Vector(target.blockX, target.blockY, target.blockZ))
                        }
                    }
                }
                // 调用回调函数
                call(ResultNavigation(pointList, startTime, scheduleTime))
            } else {
                var vec: Vector? = null
                // 重复最多 10 次的游荡请求
                repeat(10) {
                    if (vec == null) {
                        vec = RandomPositionGenerator.generateLand(NodeEntity(start, pathType.height, pathType.width), 10, 7)
                    }
                }
                if (vec != null) {
                    call(ResultRandomPosition(vec, startTime, scheduleTime))
                }
            }
        }
    }
}