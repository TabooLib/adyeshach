package ink.ptms.adyeshach.internal.command

import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.util.lite.Numbers
import io.izzel.taboolib.util.lite.cooldown.Cooldown
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface Helper {

    fun CommandSender.done(message: String) {
        toDone(this, message)
    }

    fun CommandSender.info(message: String) {
        toInfo(this, message)
    }

    fun CommandSender.error(message: String) {
        toError(this, message)
    }

    fun toInfo(sender: CommandSender, message: String) {
        sender.sendMessage("§c[Adyeshach] §7${message.replace("&", "§")}")
        if (sender is Player && !cooldown.isCooldown(sender.name)) {
            sender.playSound(sender.location, Sound.UI_BUTTON_CLICK, 1f, Numbers.getRandomDouble(1, 2).toFloat())
        }
    }

    fun toError(sender: CommandSender, message: String) {
        sender.sendMessage("§c[Adyeshach] §7${message.replace("&", "§")}")
        if (sender is Player && !cooldown.isCooldown(sender.name)) {
            sender.playSound(sender.location, Sound.ENTITY_VILLAGER_NO, 1f, Numbers.getRandomDouble(1, 2).toFloat())
        }
    }

    fun toDone(sender: CommandSender, message: String) {
        sender.sendMessage("§c[Adyeshach] §7${message.replace("&", "§")}")
        if (sender is Player && !cooldown.isCooldown(sender.name)) {
            sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, Numbers.getRandomDouble(1, 2).toFloat())
        }
    }

    companion object {

        @TInject
        val cooldown = Cooldown("adyeshach:command", 100)
    }

}