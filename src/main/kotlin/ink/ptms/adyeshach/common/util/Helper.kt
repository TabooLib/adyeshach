package ink.ptms.adyeshach.common.util

import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.SubscribeEvent
import taboolib.common.util.random
import taboolib.common5.Baffle
import java.util.concurrent.TimeUnit

fun CommandSender.info(message: String) {
    sendMessage("§c[Adyeshach] §7${message.replace("&", "§")}")
    if (this is Player && Helper.cooldown.hasNext(name)) {
        playSound(location, Sound.UI_BUTTON_CLICK, 1f, random(1, 2).toFloat())
    }
}

fun CommandSender.done(message: String) {
    sendMessage("§c[Adyeshach] §7${message.replace("&", "§")}")
    if (this is Player && Helper.cooldown.hasNext(name)) {
        playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, random(1, 2).toFloat())
    }
}

fun CommandSender.error(message: String) {
    sendMessage("§c[Adyeshach] §7${message.replace("&", "§")}")
    if (this is Player && Helper.cooldown.hasNext(name)) {
        playSound(location, Sound.ENTITY_VILLAGER_NO, 1f, random(1, 2).toFloat())
    }
}

internal object Helper {

    val cooldown = Baffle.of(100, TimeUnit.MILLISECONDS)

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        cooldown.reset(e.player.name)
    }
}