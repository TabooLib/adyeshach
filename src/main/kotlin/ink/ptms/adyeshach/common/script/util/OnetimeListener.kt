package ink.ptms.adyeshach.common.script.util

import ink.ptms.adyeshach.Adyeshach
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventException
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor

/**
 * @Author IzzelAliz
 */
class OnetimeListener<T : Event>(private val clazz: Class<T>, private val predicate: (T) -> Boolean, private val consumer: (T) -> Unit) : Listener, EventExecutor {

    override fun execute(listener: Listener, event: Event) {
        try {
            val cast = clazz.cast(event)
            if (predicate.invoke(cast)) {
                consumer.invoke(cast)
            }
        } catch (ignore: ClassCastException) {
        } catch (e: Exception) {
            throw EventException(e)
        }
    }

    companion object {

        @JvmStatic
        operator fun <T : Event> set(clazz: Class<T>, predicate: (T) -> Boolean, consumer: (T) -> Unit): OnetimeListener<T> {
            val listener = OnetimeListener(clazz, predicate, consumer)
            Bukkit.getPluginManager().registerEvent(clazz, listener, EventPriority.NORMAL, listener, Adyeshach.plugin)
            return listener
        }
    }
}