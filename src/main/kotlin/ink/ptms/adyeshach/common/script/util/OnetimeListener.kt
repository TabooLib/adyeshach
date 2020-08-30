package ink.ptms.adyeshach.common.script.util

import ink.ptms.adyeshach.Adyeshach
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventException
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * @Author IzzelAliz
 */
class OnetimeListener<T : Event>(private val clazz: Class<T>, private val predicate: Predicate<T>, private val consumer: Consumer<T>) : Listener, EventExecutor {

    @Throws(EventException::class)
    override fun execute(listener: Listener, event: Event) {
        try {
            val cast = clazz.cast(event)
            if (predicate.test(cast)) {
                consumer.accept(cast)
            }
        } catch (e: Exception) {
            throw EventException(e)
        }
    }

    companion object {

        @JvmStatic
        operator fun <T : Event> set(clazz: Class<T>, predicate: Predicate<T>, consumer: Consumer<T>): OnetimeListener<T> {
            val listener = OnetimeListener(clazz, predicate, consumer)
            Bukkit.getPluginManager().registerEvent(clazz, listener, EventPriority.NORMAL, listener, Adyeshach.plugin)
            return listener
        }
    }
}