package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.data.DataWatcher
import ink.ptms.adyeshach.common.entity.editor.MetaEditor
import org.bukkit.entity.Player

abstract class Meta<T : EntityInstance>(val index: Int, val key: String, val def: Any, var editor: MetaEditor<T>? = null) {

    lateinit var dataWatcher: DataWatcher
        protected set

    abstract fun generateMetadata(player: Player, entityInstance: EntityInstance): Any?

    fun updateEntityMetadata(entityInstance: EntityInstance) {
        entityInstance.forViewers {
            NMS.INSTANCE.updateEntityMetadata(it, entityInstance.index, generateMetadata(it, entityInstance) ?: return@forViewers)
        }
    }

    override fun toString(): String {
        return "Meta(index=$index, key='$key', def=$def)"
    }
}