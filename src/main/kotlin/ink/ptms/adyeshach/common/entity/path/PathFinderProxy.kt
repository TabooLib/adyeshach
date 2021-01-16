package ink.ptms.adyeshach.common.entity.path

import ink.ptms.adyeshach.api.Settings
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.internal.mirror.Mirror
import io.izzel.taboolib.Version
import io.izzel.taboolib.module.ai.SimpleAiSelector
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import io.izzel.taboolib.util.lite.Effects
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.world.ChunkUnloadEvent
import java.util.concurrent.ConcurrentHashMap

/**
 * @Author sky
 * @Since 2020-08-13 16:31
 */
object PathFinderProxy {

    val version = Version.getCurrentVersionInt()
    val proxyEntity = ConcurrentHashMap<String, PathEntity>()

    fun request(start: Location, target: Location, pathType: PathType = PathType.WALK_2, request: Request = Request.NAVIGATION, call: (Result) -> (Unit)) {
        if (pathType.supportVersion > version) {
            throw RuntimeException("PathType \"$pathType\" not supported this minecraft version.")
        }
        if (start.world!!.name != target.world!!.name) {
            throw PathException("cannot request navigation in different worlds.")
        }
        val proxyEntity = proxyEntity[start.world!!.name] ?: throw PathException("navigation proxy did not complete initialization.")
        proxyEntity.schedule.add(PathSchedule(start, target, pathType, request, call))
    }

    fun isProxyEntity(id: Int): Boolean {
        return proxyEntity.values.any { it.entity.values.any { entity -> entity.entityId == id } }
    }

    @TPacket(type = TPacket.Type.SEND)
    private fun packet(player: Player, packet: Packet): Boolean {
        if (packet.`is`("PacketPlayOutSpawnEntityLiving") && isProxyEntity(packet.read("a", Int::class.java)) && !Settings.get().debug) {
            return false
        }
        return true
    }

    @TFunction.Cancel
    private fun cancel() {
        proxyEntity.values.forEach { it.entity.values.forEach { entity -> entity.remove() } }
    }

    @TSchedule(period = 100)
    private fun check() {
        if (!Settings.get().pathfinderProxy) {
            return
        }
        Bukkit.getWorlds().forEach {
            Mirror.get("PathFinderProxy:onCheck:${it.name}").check {
                val loc = it.spawnLocation.clone().also { loc ->
                    loc.y = 0.0
                }
                val pathEntity = proxyEntity.computeIfAbsent(it.name) { PathEntity() }
                for (pathType in PathType.values()) {
                    // 版本允许
                    if (pathType.supportVersion <= version) {
                        // 实体不存在或失效
                        if (!pathEntity.entity.containsKey(pathType) || !pathEntity.entity[pathType]!!.isValid) {
                            if (version >= 11200) {
                                it.spawn(loc, pathType.entity) { entity ->
                                    entity.customName = "Adyeshach Pathfinder Proxy"
                                    pathEntity.entity.put(pathType, entity as Creature)?.remove()
                                }.silent()
                            } else {
                                (NMS.INSTANCE.addEntity(loc, pathType.entity) { entity ->
                                    entity.customName = "Adyeshach Pathfinder Proxy"
                                    pathEntity.entity.put(pathType, entity as Creature)?.remove()
                                } as Creature).silent()
                            }
                        }
                    }
                }
            }
        }
    }

    @TSchedule(period = 2)
    private fun schedule() {
        proxyEntity.forEach { (world, pathEntity) ->
            if (pathEntity.schedule.isEmpty()) {
                return@forEach
            }
            Mirror.get("PathFinderProxy:onSchedule:$world").check {
                pathEntity.schedule.forEach { schedule ->
                    val entity = pathEntity.entity[schedule.pathType]
                    if (entity != null) {
                        val time = System.currentTimeMillis()
                        entity.fallDistance = 0f
                        entity.setAI(true)
                        entity.teleport(schedule.start)
                        when (schedule.request) {
                            Request.NAVIGATION -> {
                                if (Settings.get().debug) {
                                    println("[Adyeshach DEBUG] Retry (${schedule.retry}) NAVIGATION ${schedule.pathType}")
                                }
                                val pathList = NMS.INSTANCE.getNavigationPathList(entity, schedule.target)
                                if (pathList.isNotEmpty() || schedule.retry++ > 4) {
                                    schedule.call.invoke(ResultNavigation(pathList, schedule.beginTime, time))
                                    pathEntity.schedule.remove(schedule)
                                    entity.setAI(false)
                                    entity.teleport(entity.world.spawnLocation.clone().also { loc ->
                                        loc.y = 0.0
                                    })
                                    if (Settings.get().debug) {
                                        pathList.forEach {
                                            Effects.create(Particle.VILLAGER_HAPPY, Location(entity.world, it.x + 0.5, it.y + 0.5, it.z + 0.5))
                                                .count(10)
                                                .range(100.0)
                                                .play()
                                        }
                                    }
                                }
                            }
                            Request.RANDOM_POSITION -> {
                                if (Settings.get().debug) {
                                    println("[Adyeshach DEBUG] Retry (${schedule.retry}) RANDOM_POSITION ${schedule.pathType}")
                                }
                                val position = NMS.INSTANCE.generateRandomPosition(entity, entity.location.block.isLiquid)
                                if (position != null || schedule.retry++ > 4) {
                                    schedule.call.invoke(ResultRandomPosition(position, schedule.beginTime, time))
                                    pathEntity.schedule.remove(schedule)
                                    entity.setAI(false)
                                    entity.teleport(Location(entity.world, 0.0, 0.0, 0.0))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Creature.silent(): Creature {
        isSilent = true
        isCollidable = false
        isInvulnerable = true
        setAI(false)
        SimpleAiSelector.getExecutor().clearGoalAi(this)
        SimpleAiSelector.getExecutor().clearTargetAi(this)
        getAttribute(Attribute.GENERIC_FOLLOW_RANGE)?.baseValue = 100.0
        return this
    }

    @TListener
    class PathListener : Listener {

        @EventHandler
        fun e(e: EntityDeathEvent) {
            if (isProxyEntity(e.entity.entityId)) {
                e.drops.clear()
                e.droppedExp = 0
            }
        }

        @EventHandler
        fun e(e: ChunkUnloadEvent) {
            if (version < 11400 && e.chunk == Location(e.chunk.world, 0.0, 0.0, 0.0).chunk) {
                (e as Cancellable).isCancelled = true
            }
        }
    }
}