package ink.ptms.adyeshach.common.path

import org.bukkit.entity.Mob
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class PathEntity {

    val entity = ConcurrentHashMap<PathType, Mob>()
    val schedule = CopyOnWriteArrayList<PathSchedule>()
    val spawnFailed = CopyOnWriteArrayList<PathType>()

}