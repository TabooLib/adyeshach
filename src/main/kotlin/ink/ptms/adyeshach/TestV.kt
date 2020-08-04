package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.entity.MetadataExtend
import ink.ptms.adyeshach.common.entity.element.EntityRotation
import ink.ptms.adyeshach.common.entity.type.impl.AdyAreaEffectCloud
import ink.ptms.adyeshach.common.entity.type.impl.AdyArmorStand
import ink.ptms.adyeshach.common.entity.type.impl.AdyArrow
import ink.ptms.adyeshach.common.entity.type.impl.AdyBat
import ink.ptms.adyeshach.common.util.Serializer
import ink.ptms.adyeshach.nms.NMS
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import net.minecraft.server.v1_16_R1.PacketPlayOutSpawnEntityLiving
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle

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
                if (sender is Player) {
                    val entity = AdyArrow(sender)
                    entity.spawn(sender.location)
                    entity.setPiercingLevel(1)
                    entity.setCritical(true)
                    entity.setGlowing(true)
                    entity.setColor(Color.RED)
                    NMS.INSTANCE.updateEntityMetadata(entity.owner, entity.index, *entity.metadata().toTypedArray())
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
                        entity.destroy()
                        sender.sendMessage("§c[System] §7Removed.")
                    }, 60L)
                    sender.sendMessage("§c[System] §7Done.")
                }
            }
}