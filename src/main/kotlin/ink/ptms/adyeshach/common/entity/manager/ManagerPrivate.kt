package ink.ptms.adyeshach.common.entity.manager

import com.google.common.collect.Lists
import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-08-14 14:25
 */
class ManagerPrivate(val player: Player) {

    val activeEntity = Lists.newCopyOnWriteArrayList<EntityInstance>()

    fun onLoad() {

    }

    fun onSave() {

    }

    fun onTick() {

    }

    fun onSpawn(entityInstance: EntityInstance) {

    }

    fun onDestroy(entityInstance: EntityInstance) {

    }
}