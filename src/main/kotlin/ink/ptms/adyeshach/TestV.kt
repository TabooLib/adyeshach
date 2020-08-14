package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.path.PathFinderProxy
import ink.ptms.adyeshach.common.path.PathType
import io.izzel.taboolib.module.ai.SimpleAiSelector
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.util.Reflection
import io.izzel.taboolib.util.lite.Effects
import net.minecraft.server.v1_16_R1.*
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftSilverfish
import org.bukkit.entity.Player
import org.bukkit.entity.Silverfish

/**
 * @author Arasple
 * @date 2020/8/3 21:55
 * Villager test
 */
object TestV {

    var proxy: Silverfish? = null

    @TInject
    val testV: CommandBuilder = CommandBuilder
            .create("test", Adyeshach.plugin)
            .execute { sender, _ ->
                if (sender is Player) {
                    PathFinderProxy.request(sender.location, sender.location) {
                        it.pointList.forEach { point ->
                            // logic ...
                        }
                    }

//                    if (proxy == null) {
//                        proxy = sender.world.spawn(sender.location, Silverfish::class.java)
//                        proxy!!.isSilent = true
//                        proxy!!.isCollidable = false
//                        proxy!!.isInvulnerable = true
//
//                        SimpleAiSelector.getExecutor().clearTargetAi(proxy)
//                        SimpleAiSelector.getExecutor().clearGoalAi(proxy)
//                        sender.sendMessage(" proxy created.")
//                    } else{
//                        val pathEntity = (proxy as CraftSilverfish).handle.navigation.a(BlockPosition(sender.location.blockX, sender.location.blockY, sender.location.blockZ), 1)
//                        if (pathEntity != null) {
//                            // field a
//                            pathEntity.d().forEach {
//                                Effects.create(Particle.VILLAGER_HAPPY, Location(sender.world, it.a().x + 0.5, it.a().y + 0.5, it.a().z + 0.5)).count(10).range(100.0).play()
//                            }
//                            proxy!!.teleport(sender.location)
//                            sender.sendMessage(" path created.")
//                        } else {
//                            sender.sendMessage(" path not found.")
//                        }
//                    }




//                    val pathfinderNormal = PathfinderNormal()
//                    // enter door
//                    pathfinderNormal.a(true)
//
//                    val pathfinder = Pathfinder(pathfinderNormal, 1)
//                    val pathPoint = PathPoint(sender.location.blockX, sender.location.blockY, sender.location.blockZ)
//                    pathPoint.l = PathType.WALKABLE
//                    pathPoint.k = 1.0f
//
//                    // target
//                    val blockPosition = BlockPosition(sender.location.blockX + 10, sender.location.blockY, sender.location.blockZ)
//                    val pathDestination = pathfinderNormal.a(sender.location.x + 10, sender.location.y, sender.location.z)
//
//                    val pathEntity = Reflection.invokeMethod(pathfinder, "a", pathPoint, mapOf(pathDestination to blockPosition), 100f, 1, 1f)
//
//                    sender.sendMessage("§c[System] §7Done. ${pathEntity}")
                }
            }

}