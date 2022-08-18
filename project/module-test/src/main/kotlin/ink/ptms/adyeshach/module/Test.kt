package ink.ptms.adyeshach.module

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyEntity
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest

@CommandHeader(name = "adytest")
object Test {

    lateinit var entity: AdyEntity

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
        dynamic {
            suggest { EntityTypes.values().map { it.name } }
            execute<Player> { sender, _, args ->
                if (::entity.isInitialized) {
                    entity.despawn(removeFromManager = true)
                }

                val npc = Adyeshach.api().getPublicEntityManager().create(EntityTypes.valueOf(args.uppercase()), sender.location) as AdyEntity
                if (npc is AdyHuman) {
                    npc.setName("坏黑")
                    npc.setTexture("bukkitObj")
                }
                npc.setCustomNameVisible(true)
                npc.setGlowing(true)

                entity = npc
                sender.info("OK")
                sender.info(npc.viewPlayers)
            }
        }
    }

    @CommandBody
    val test3 = subCommand {
        execute<Player> { sender, _, _ ->
            entity.despawn(removeFromManager = true)

            sender.info("OK")
        }
    }
}