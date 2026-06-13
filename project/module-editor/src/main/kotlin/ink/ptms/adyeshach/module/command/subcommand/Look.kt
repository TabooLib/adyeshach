@file:Suppress("DuplicatedCode")

package ink.ptms.adyeshach.module.command.subcommand

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.util.sendLang
import ink.ptms.adyeshach.core.util.submitRepeat
import ink.ptms.adyeshach.module.command.*
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation

private const val STANDARD_LOOK_TRACKER = "look"

private const val CONTROLLER_LOOK_TICKS = 5

/**
 * npc look (id)? (method) (...)?
 *
 * npc look 1 here
 * npc look 1 to world 0 0 0 —— 移动到指定位置
 */
val lookSubCommand = subCommand {
    dynamic("id") {
        suggestEntityList()
        // 看向当前位置（兼容旧命令，等同 here-b）
        literal("here") {
            execute<Player> { sender, ctx, _ ->
                runLookHere(sender, ctx["id"], LookMode.HEAD_ONLY)
            }
        }
        literal("here-a") {
            execute<Player> { sender, ctx, _ ->
                runLookHere(sender, ctx["id"], LookMode.HEAD_AND_BODY)
            }
        }
        literal("here-b") {
            execute<Player> { sender, ctx, _ ->
                runLookHere(sender, ctx["id"], LookMode.HEAD_ONLY)
            }
        }
        literal("here-c") {
            execute<Player> { sender, ctx, _ ->
                runLookHere(sender, ctx["id"], LookMode.CONTROLLER)
            }
        }
        // 学习我的方向（兼容旧命令，等同 like-b）
        literal("like") {
            execute<Player> { sender, ctx, _ ->
                runLookLike(sender, ctx["id"], LookMode.HEAD_ONLY)
            }
        }
        literal("like-a") {
            execute<Player> { sender, ctx, _ ->
                runLookLike(sender, ctx["id"], LookMode.HEAD_AND_BODY)
            }
        }
        literal("like-b") {
            execute<Player> { sender, ctx, _ ->
                runLookLike(sender, ctx["id"], LookMode.HEAD_ONLY)
            }
        }
        literal("like-c") {
            execute<Player> { sender, ctx, _ ->
                runLookLike(sender, ctx["id"], LookMode.CONTROLLER)
            }
        }
        // 看向指定方向
        literal("with").euler {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_LOOK_TRACKER, unified = false) {
                    val origin = it.getLocation().toProxyLocation()
                    val y = ctx.yaw("yaw", origin)
                    val p = ctx.pitch("pitch", origin)
                    it.setHeadRotation(y, p)
                    if (!sender.isIgnoreNotice()) {
                        sender.sendLang("command-look-with", it.id, format(y), format(p))
                    }
                }
            }
        }
        // 看向指定位置
        literal("to").xyz {
            execute<CommandSender> { sender, ctx, _ ->
                multiControl<EntitySource.Empty>(sender, ctx["id"], STANDARD_LOOK_TRACKER, unified = false) {
                    val loc = ctx.locationWithoutWorld(origin = it.getLocation().toProxyLocation())
                    it.setHeadRotation(loc.toBukkitLocation())
                    if (!sender.isIgnoreNotice()) {
                        sender.sendLang("command-look-to-location", it.id, format(loc.x), format(loc.y), format(loc.z))
                    }
                }
            }
        }
    }
}

private enum class LookMode {
    HEAD_AND_BODY,
    HEAD_ONLY,
    CONTROLLER,
}

private fun runLookHere(sender: Player, id: String, mode: LookMode) {
    multiControl<EntitySource.Empty>(sender, id, STANDARD_LOOK_TRACKER, unified = false) {
        if (it.world != sender.world) {
            sender.sendLang("command-world-different", id)
            return@multiControl
        }
        applyLookAtTarget(it, mode, sender.eyeLocation)
        if (!sender.isIgnoreNotice()) {
            sender.sendLang("command-look-to-here", it.id)
        }
    }
}

private fun runLookLike(sender: Player, id: String, mode: LookMode) {
    multiControl<EntitySource.Empty>(sender, id, STANDARD_LOOK_TRACKER, unified = false) {
        if (it.world != sender.world) {
            sender.sendLang("command-world-different", id)
            return@multiControl
        }
        applyLookDirection(it, mode, sender)
        if (!sender.isIgnoreNotice()) {
            sender.sendLang("command-look-with", it.id, format(sender.location.yaw), format(sender.location.pitch))
        }
    }
}

private fun applyLookAtTarget(entity: EntityInstance, mode: LookMode, target: Location) {
    when (mode) {
        LookMode.HEAD_AND_BODY -> entity.setHeadAndBodyRotation(target)
        LookMode.HEAD_ONLY -> entity.setHeadRotation(target)
        LookMode.CONTROLLER -> {
            submitRepeat(CONTROLLER_LOOK_TICKS) {
                entity.controllerLookAt(target.x, target.y, target.z, 35f, 40f)
            }
        }
    }
}

private fun applyLookDirection(entity: EntityInstance, mode: LookMode, sender: Player) {
    when (mode) {
        LookMode.HEAD_AND_BODY -> entity.setHeadAndBodyRotation(sender.location.yaw, sender.location.pitch)
        LookMode.HEAD_ONLY -> entity.setHeadRotation(sender.location.yaw, sender.location.pitch)
        LookMode.CONTROLLER -> {
            // controllerLookAt 只接收目标点，学视角时用实体眼位沿玩家视线外推成目标点。
            val target = entity.getEyeLocation().clone().add(sender.location.direction.multiply(16.0))
            submitRepeat(CONTROLLER_LOOK_TICKS) {
                entity.controllerLookAt(target.x, target.y, target.z, 35f, 40f)
            }
        }
    }
}
