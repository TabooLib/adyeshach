package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.path.PathFinderProxy
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.util.lite.Effects
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/3 21:55
 * Villager test
 */
object TestV {

    @TInject
    val testV: CommandBuilder = CommandBuilder
            .create("test", Adyeshach.plugin)
            .execute { sender, _ ->
                if (sender is Player) {
                    PathFinderProxy.request(sender.location, sender.getTargetBlockExact(50)!!.location) {
                        it.pointList.forEach { point ->
                            Effects.create(Particle.VILLAGER_HAPPY, Location(sender.world, point.x + 0.5, point.y + 0.5, point.z + 0.5)).count(10).range(100.0).play()
                        }
                        sender.sendMessage(" wait: ${it.waitTime} ms, cost: ${it.costTime} ms")
                    }
                    sender.sendMessage("requested.")
                }
            }

}