package ink.ptms.adyeshach.common.path

import io.netty.util.internal.ConcurrentSet
import org.bukkit.entity.Creature
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class PathEntity {

    val entity = ConcurrentHashMap<PathType, Creature>()
    val schedule = CopyOnWriteArrayList<PathSchedule>()

}