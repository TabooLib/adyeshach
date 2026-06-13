package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.module.command.EntitySource
import ink.ptms.adyeshach.module.command.isIgnoreNotice
import ink.ptms.adyeshach.module.command.multiControl
import ink.ptms.adyeshach.module.command.suggestEntityList
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

/**
 * npc respawn (id)?
 *
 * 对实体执行整包 respawn（销毁后按当前观察者重新生成）
 */
val respawnSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        execute<CommandSender> { sender, ctx, _ ->
            multiControl<EntitySource.Empty>(sender, ctx.self(), "respawn", unified = false) { entity ->
                entity.respawn()
                if (sender is Player && !sender.isIgnoreNotice()) {
                    sender.sendLang("command-respawn-success", entity.id)
                }
            }
        }
    }
    execute<Player> { sender, _, _ ->
        multiControl<EntitySource.Empty>(sender, "respawn") { entity ->
            entity.respawn()
            if (!sender.isIgnoreNotice()) {
                sender.sendLang("command-respawn-success", entity.id)
            }
        }
    }
}