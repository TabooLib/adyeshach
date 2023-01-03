package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.bukkit.data.DataWatcher
import ink.ptms.adyeshach.core.entity.Meta
import org.bukkit.entity.Player

@Deprecated("Outdated but usable")
abstract class Meta<T>(val index: Int, val key: String, val def: Any, val v2: Meta<*>) {

    lateinit var dataWatcher: DataWatcher
        protected set

    abstract fun generateMetadata(player: Player, entityInstance: EntityInstance): Any?

    fun updateEntityMetadata(entityInstance: EntityInstance) {
        v2.updateEntityMetadata(entityInstance.v2)
    }

    override fun toString(): String {
        return "Meta(index=$index, key='$key', def=$def)"
    }
}