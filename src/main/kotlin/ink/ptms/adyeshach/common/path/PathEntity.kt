package ink.ptms.adyeshach.common.path

import org.bukkit.entity.Creature
import org.bukkit.entity.Mob
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class PathEntity {

    val entity = ConcurrentHashMap<PathType, Creature>()
    val schedule = CopyOnWriteArrayList<PathSchedule>()
    val spawnFailed = CopyOnWriteArrayList<PathType>()

}