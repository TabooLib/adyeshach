package ink.ptms.adyeshach.module

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.manager.create
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand

@CommandHeader(name = "adytest")
object Test {

    @CommandBody
    val test1 = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.info("MinecraftEntityMetadataHandler")
            testParser(sender)
            testCreateMeta(sender)
            sender.info("MinecraftHelper")
            testAdapter(sender)
        }
    }

    @CommandBody
    val test2 = subCommand {
        execute<Player> { sender, _, _ ->
//            Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityLiving(
//                sender,
//                EntityTypes.ARMOR_STAND,
//                Indexs.nextIndex(),
//                UUID.randomUUID(),
//                sender.location
//            )

            val armorStand = Adyeshach.api().getPublicEntityManager().create<AdyArmorStand>(sender.location)
            armorStand.setCustomName("test")
            armorStand.setCustomNameVisible(true)

            sender.info("OK")
            sender.info(armorStand.viewPlayers)
        }
    }
}