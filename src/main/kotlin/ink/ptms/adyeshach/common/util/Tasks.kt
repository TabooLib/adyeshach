package ink.ptms.adyeshach.common.util

import ink.ptms.adyeshach.Adyeshach
import org.bukkit.Bukkit

/**
 * @author Arasple
 * @date 2020/2/27 9:30
 */
object Tasks {

    fun task(runnable: () -> (Unit)) = task(false, runnable)

    fun task(async: Boolean, runnable: () -> (Unit)) = if (async) Bukkit.getScheduler().runTaskAsynchronously(Adyeshach.plugin, runnable.toRunnable()) else Bukkit.getScheduler().runTask(Adyeshach.plugin, runnable.toRunnable())

    fun delay(delay: Long, runnable: () -> (Unit)) = delay(delay, false, runnable)

    fun delay(delay: Long, async: Boolean, runnable: () -> (Unit)) = if (async) Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, runnable.toRunnable(), delay) else Bukkit.getScheduler().runTaskLater(Adyeshach.plugin, runnable.toRunnable(), delay)

    fun timer(delay: Long, period: Long, runnable: () -> (Unit)) = timer(delay, period, false, runnable)

    fun timer(delay: Long, period: Long, async: Boolean, runnable: () -> (Unit)) = if (async) Bukkit.getScheduler().runTaskTimerAsynchronously(Adyeshach.plugin, runnable.toRunnable(), delay, period) else Bukkit.getScheduler().runTaskTimer(Adyeshach.plugin, runnable.toRunnable(), delay, period)

    private fun (() -> (Unit)).toRunnable(): Runnable {
        return Runnable { this.invoke() }
    }
}