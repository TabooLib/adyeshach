package ink.ptms.adyeshach.internal.command

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.internal.migrate.Migrate
import ink.ptms.adyeshach.internal.mirror.Mirror
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.kotlin.navigation.Navigation
import io.izzel.taboolib.kotlin.navigation.pathfinder.NodeEntity
import io.izzel.taboolib.module.command.base.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.LocalTime

/**
 * @Author sky
 * @Since 2020-08-05 0:05
 */
@BaseCommand(name = "adyeshachtest", aliases = ["atest"], permission = "adyeshach.command")
class CommandTest : BaseMainCommand(), Helper {

    @SubCommand(description = "verify the entity type.")
    var verify: BaseSubCommand = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            sender.info("Checking...")
            sender.info("  EntityTypes:")
            EntityTypes.values().forEach {
                try {
                    NMS.INSTANCE.getEntityTypeNMS(it)
                    sender.info("    §f$it &aSUPPORTED")
                } catch (t: Throwable) {
                    sender.info("    §f$it &cERROR")
                }
            }
            sender.info("Done.")
        }
    }

    @SubCommand(description = "test random position generator.", type = CommandType.PLAYER)
    var randomStrollLand: BaseSubCommand = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            val generateLand = Navigation.randomPositionGenerator().generateLand(NodeEntity((sender as Player).location, 2.0, 1.0), 10, 7)
            if (generateLand == null) {
                sender.sendMessage("[Adyeshach] No Way ${LocalTime.now().second}")
            } else {
                sender.sendMessage("[Adyeshach] Found Way $generateLand")
            }
        }
    }

    @SubCommand(description = "migrate from the other.")
    var migrate: BaseSubCommand = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("type") { Migrate.migrates.filter { it.value.isEnabled() }.map { it.key }.toList() })
        }

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            val migrate = Migrate.migrates[args[0]]
            if (migrate == null || !migrate.isEnabled()) {
                sender.error("Migrate Type ${args[0]} not registered.")
                return
            }
            val time = System.currentTimeMillis()
            sender.info("Translating...")
            migrate.migrate()
            sender.info("Successfully. (${System.currentTimeMillis() - time}ms)")
        }
    }

    @SubCommand(description = "print performance monitoring.")
    var mirror: BaseSubCommand = object : BaseSubCommand() {

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            sender.info("Creating...")
            sender.info("---")
            Tasks.task(true) {
                Mirror.collect().run {
                    print(sender, getTotal(), 0)
                }
                sender.info("---")
            }
        }
    }
}