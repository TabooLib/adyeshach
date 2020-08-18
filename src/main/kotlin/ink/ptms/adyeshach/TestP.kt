package ink.ptms.adyeshach

import com.mojang.datafixers.util.Pair
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import net.minecraft.server.v1_16_R1.EnumItemSlot
import net.minecraft.server.v1_16_R1.ItemStack
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/3 22:34
 * Player (NPC) test
 */
@Suppress("UNCHECKED_CAST")
object TestP {

    @TInject
    val testV: CommandBuilder = CommandBuilder
            .create("test-p", Adyeshach.plugin)
            .execute { sender, _ ->
                if (sender is Player) {
                    sender.sendMessage("§c[System] §7Done.")
                }
            }
}