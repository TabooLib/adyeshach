package ink.ptms.adyeshach.common.script

import io.izzel.taboolib.util.KV
import org.bukkit.event.Event
import kotlin.reflect.KClass

/**
 * @Author sky
 * @Since 2020-08-30 19:22
 */
class KnownEvent<T : Event>(val eventClass: KClass<out T>) {

    val field = HashMap<String, Pair<Get<T>, Set<T>>>()

    fun unit(name: String, builder: Builder<T>.() -> Unit): KnownEvent<T> {
        field[name] = Builder(name, this).run {
            builder(this)
            Get(get) to Set(set)
        }
        return this
    }

    class Get<T>(val func: (T) -> Any?)

    class Set<T>(val func: (T, Any?) -> Unit)

    class Builder<T : Event>(val name: String, val ke: KnownEvent<T>) {

        var get: (T) -> Any? = { null }
        var set: (T, Any?) -> Unit = { _, _ -> }
    }
}