package ink.ptms.adyeshach.common.script

import io.izzel.taboolib.util.KV
import org.bukkit.event.Event
import kotlin.reflect.KClass

/**
 * @Author sky
 * @Since 2020-08-30 19:22
 */
class KnownEvent<T: Event>(val eventClass: KClass<out T>) {

    val field = HashMap<String, KV<(T) -> (Any?), (T, Any?) -> (Unit)>>()

    fun field(name: String, get: (T) -> (Any?), set: (T, Any?) -> (Unit) = { _, _ -> }): KnownEvent<T> {
        field[name] = KV(get, set)
        return this
    }
}