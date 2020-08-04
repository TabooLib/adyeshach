package ink.ptms.adyeshach.common.util

import ink.ptms.adyeshach.Adyeshach
import org.bukkit.Bukkit

/**
 * @author Arasple
 * @date 2020/2/27 9:30
 */
object Tasks {

    fun task(runnable: Runnable) = task(false, runnable)

    fun task(async: Boolean, runnable: Runnable) = if (async) Bukkit.getScheduler().runTaskAsynchronously(Adyeshach.plugin, runnable) else Bukkit.getScheduler().runTask(Adyeshach.plugin, runnable)

    fun delay(delay: Long, runnable: Runnable) = delay(delay, false, runnable)

    fun delay(delay: Long, async: Boolean, runnable: Runnable) = if (async) Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, runnable, delay) else Bukkit.getScheduler().runTaskLater(Adyeshach.plugin, runnable, delay)

    fun timer(delay: Long, period: Long, runnable: Runnable) = timer(delay, period, false, runnable)

    fun timer(delay: Long, period: Long, async: Boolean, runnable: Runnable) = if (async) Bukkit.getScheduler().runTaskTimerAsynchronously(Adyeshach.plugin, runnable, delay, period) else Bukkit.getScheduler().runTaskTimer(Adyeshach.plugin, runnable, delay, period)

}