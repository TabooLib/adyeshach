package ink.ptms.adyeshach.common.bukkit.data

@Deprecated("Outdated and unusable")
abstract class DataWatcher {

    abstract fun parse(value: Any): Any

    abstract fun createMetadata(index: Int, value: Any): Any
}