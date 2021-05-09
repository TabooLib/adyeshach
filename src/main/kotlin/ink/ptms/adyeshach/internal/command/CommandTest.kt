package ink.ptms.adyeshach.internal.command

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.internal.migrate.Migrate
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.module.command.base.*
import org.bukkit.command.CommandSender

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
                AdyeshachAPI.mirror.collectAndReport(sender)
                sender.info("---")
            }
        }
    }
}