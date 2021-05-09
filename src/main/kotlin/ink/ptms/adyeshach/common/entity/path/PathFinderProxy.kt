package ink.ptms.adyeshach.common.entity.path

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI.mirrorFuture
import ink.ptms.adyeshach.api.Settings
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.Version
import io.izzel.taboolib.kotlin.navigation.Navigation
import io.izzel.taboolib.kotlin.navigation.pathfinder.NodeEntity
import io.izzel.taboolib.module.ai.SimpleAiSelector
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import io.izzel.taboolib.util.lite.Effects
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @Author sky
 * @Since 2020-08-13 16:31
 */
object PathFinderProxy {

    val version = Version.getCurrentVersionInt()
    val entities = ConcurrentHashMap<UUID, Creature>()
    val requests = CopyOnWriteArrayList<PathSchedule>()

    fun request(start: Location, target: Location, pathType: PathType = PathType.WALK_2, request: Request = Request.NAVIGATION, call: (Result) -> (Unit)) {
        if (pathType.supportVersion > version) {
            throw RuntimeException("PathType \"$pathType\" not supported this minecraft version.")
        }
        if (start.world!!.name != target.world!!.name) {
            throw PathException("cannot request navigation in different worlds.")
        }
        val startTime = System.currentTimeMillis()
        when (Adyeshach.settings.pathfinder) {
            Settings.Pathfinder.PROXY -> {
                requests.add(PathSchedule(start, target, pathType, request, call))
            }
            Settings.Pathfinder.NATIVE -> {
                Tasks.task(!Settings.get().pathfinderSync) {
                    if (request == Request.NAVIGATION) {
                        mirrorFuture("PathFinderProxy:Native:Navigation") {
                            val time = System.currentTimeMillis()
                            val pathFinder = Navigation.create(NodeEntity(start, pathType.height, pathType.width))
                            val path = pathFinder.findPath(target, distance = 32f)
                            finish()
                            if (Settings.get().debug) {
                                path?.nodes?.forEach { it.display(target.world) }
                            }
                            call(ResultNavigation(path?.nodes?.map { it.asBlockPos() } ?: emptyList(), startTime, time))
                        }
                    } else {
                        mirrorFuture("PathFinderProxy:Native:RandomPosition") {
                            val time = System.currentTimeMillis()
                            var vec: Vector? = null
                            repeat(10) {
                                if (vec == null) {
                                    vec = Navigation.randomPositionGenerator().generateLand(NodeEntity(start, pathType.height, pathType.width), 10, 7)
                                }
                            }
                            finish()
                            if (vec != null) {
                                call(ResultRandomPosition(vec, startTime, time))
                            }
                        }
                    }
                }
            }
        }
    }

    fun isProxyEntity(player: Player, id: Int): Boolean {
        return try {
            player.world.entities.toList().any { it.entityId == id && it.customName == "Adyeshach Pathfinder Proxy" }
        } catch (ignored: Exception) {
            return false
        }
    }

    @TPacket(type = TPacket.Type.SEND)
    private fun packet(player: Player, packet: Packet): Boolean {
        if (packet.`is`("PacketPlayOutSpawnEntityLiving") && isProxyEntity(player, packet.read("a", 0)) && !Settings.get().debug) {
            return false
        }
        return true
    }

    @TFunction.Cancel
    private fun cancel() {
        entities.forEach { it.value.remove() }
        entities.clear()
    }

    @TSchedule(period = 5)
    private fun schedule() {
        val groups = requests.groupBy { it.start.world }
        requests.clear()
        groups.forEach { (world, request) ->
            if (request.isEmpty()) {
                return@forEach
            }
            mirrorFuture("PathFinderProxy:onSchedule:${world.name}") {
                val entity = spawnPathfinderProxyEntity(request[0].start, request[0].pathType)
                entities[entity.uniqueId] = entity
                finish()
                Tasks.delay(2) {
                    request.forEach {
                        val time = System.currentTimeMillis()
                        entity.fallDistance = 0f
                        entity.teleport(it.start)
                        when (it.request) {
                            Request.NAVIGATION -> {
                                val pathList = NMS.INSTANCE.getNavigationPathList(entity, it.target)
                                if (pathList.isEmpty() && it.retry++ < 5) {
                                    requests.add(it)
                                } else {
                                    it.call.invoke(ResultNavigation(pathList, it.beginTime, time))
                                    if (Settings.get().debug) {
                                        pathList.forEach { p ->
                                            Effects.create(Particle.VILLAGER_HAPPY, Location(entity.world, p.x + 0.5, p.y + 0.5, p.z + 0.5))
                                                .count(10)
                                                .range(100.0)
                                                .playAsync()
                                        }
                                    }
                                }
                            }
                            Request.RANDOM_POSITION -> {
                                val position = NMS.INSTANCE.generateRandomPosition(entity, entity.location.block.isLiquid)
                                if (position == null && it.retry++ < 5) {
                                    requests.add(it)
                                } else {
                                    it.call.invoke(ResultRandomPosition(position, it.beginTime, time))
                                    if (Settings.get().debug) {
                                        val p = position!!
                                        Effects.create(Particle.FLAME, Location(entity.world, p.x + 0.5, p.y + 0.5, p.z + 0.5))
                                            .count(10)
                                            .range(100.0)
                                            .playAsync()
                                    }
                                }
                            }
                        }
                    }
                    entity.remove()
                    entities.remove(entity.uniqueId)
                }
            }
        }
    }

    private fun spawnPathfinderProxyEntity(loc: Location, pathType: PathType): Creature {
        return if (version >= 11200) {
            loc.world.spawn(loc, pathType.entity) { entity ->
                entity.customName = "Adyeshach Pathfinder Proxy"
            }.silent()
        } else {
            (NMS.INSTANCE.addEntity(loc, pathType.entity) { entity ->
                entity.customName = "Adyeshach Pathfinder Proxy"
            } as Creature).silent()
        }
    }

    private fun Creature.silent(): Creature {
        isSilent = true
        isCollidable = false
        isInvulnerable = true
        SimpleAiSelector.getExecutor().clearGoalAi(this)
        SimpleAiSelector.getExecutor().clearTargetAi(this)
        getAttribute(Attribute.GENERIC_FOLLOW_RANGE)?.baseValue = 100.0
        return this
    }

    @TListener
    class PathListener : Listener {

        @EventHandler
        fun e(e: EntityDeathEvent) {
            if (e.entity.customName == "Adyeshach Pathfinder Proxy") {
                e.drops.clear()
                e.droppedExp = 0
            }
        }
    }
}