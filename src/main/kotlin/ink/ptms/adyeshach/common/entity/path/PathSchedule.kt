package ink.ptms.adyeshach.common.entity.path

import org.bukkit.Location

/**
 * 由寻路代理实体返回的路径参数
 * 理想延迟在 150 毫秒
 */
class PathSchedule(val start: Location, val target: Location, val pathType: PathType, val request: Request, val call: (Result) -> (Unit)) {

    /**
     * 重试次数
     * 寻路代理可能因区块未加载等其他原因无法处理，会在 1 秒内重试最多 5 次。
     */
    var retry = 0
    val beginTime = System.currentTimeMillis()
}