package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.general.GeneralGravity
import ink.ptms.adyeshach.common.entity.type.AdyHorse
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import net.minecraft.server.v1_16_R1.EnumMoveType
import net.minecraft.server.v1_16_R1.Vec3D
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftVillager
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.entity.Villager

/**
 * @author Arasple
 * @date 2020/8/3 21:55
 * Villager test
 */
object TestV {

    @TInject
    val testV: CommandBuilder = CommandBuilder
        .create("test-v", Adyeshach.plugin)
        .execute { sender, _ ->
            sender as Player

            val time = System.currentTimeMillis()

            val entity = AdyeshachAPI.getEntityManagerPublicTemporary().create(EntityTypes.VILLAGER, sender.location)

            entity.pathfinder.add(GeneralGravity(entity))

            Tasks.delay(100, true) {
                entity.delete()
            }

            sender.sendMessage("done. ${System.currentTimeMillis() - time}")
        }
}