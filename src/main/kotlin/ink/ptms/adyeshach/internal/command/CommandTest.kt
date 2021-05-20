package ink.ptms.adyeshach.internal.command

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.internal.migrate.Migrate
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Coerce
import org.bukkit.command.CommandSender

/**
 * @author sky
 * @since 2020-08-05 0:05
 */
@BaseCommand(name = "adyeshachtest", aliases = ["atest"], permission = "adyeshach.command")
class CommandTest : BaseMainCommand(), Helper {

    @SubCommand(description = "@command-test-verify", priority = 0.1)
    fun verify(sender: CommandSender, args: Array<String>) {
        sender.sendLocale("command-test-verify-header")
        EntityTypes.values().forEach {
            try {
                NMS.INSTANCE.getEntityTypeNMS(it)
                sender.info("  §f$it &aSUPPORTED")
            } catch (t: Throwable) {
                sender.info("  §f$it &cERROR")
            }
        }
    }

    @SubCommand(description = "@command-test-migrate", priority = 0.2)
    var migrate: BaseSubCommand = object : BaseSubCommand() {

        override fun getArguments(): Array<Argument> {
            return of(Argument("type") { Migrate.migrates.filter { it.value.isEnabled() }.map { it.key }.toList() })
        }

        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, s: String, args: Array<String>) {
            val migrate = Migrate.migrates[args[0]]
            if (migrate == null || !migrate.isEnabled()) {
                sender.sendLocale("command-test-migrate-not-support", args[0])
                return
            }
            val time = System.currentTimeMillis()
            sender.sendLocale("command-test-migrate-header")
            migrate.migrate()
            sender.sendLocale("command-test-migrate-bottom", System.currentTimeMillis() - time)
        }
    }

    @SubCommand(description = "@command-test-mirror", arguments = ["@command-argument-reset?"], priority = 0.3)
    fun mirror(sender: CommandSender, args: Array<String>) {
        if (Coerce.toBoolean(args.getOrNull(0) ?: false)) {
            AdyeshachAPI.mirror.dataMap.clear()
            sender.sendLocale("command-test-mirror-reset")
        } else {
            TLocale.sendTo(sender, "command-test-mirror-header")
            TLocale.sendTo(sender, "command-test-mirror-bottom")
            Tasks.task(true) {
                AdyeshachAPI.mirror.collectAndReport(sender)
                TLocale.sendTo(sender, "command-test-mirror-bottom")
            }
        }
    }
}