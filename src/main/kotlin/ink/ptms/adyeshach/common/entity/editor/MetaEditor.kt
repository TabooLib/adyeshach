package ink.ptms.adyeshach.common.entity.editor

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Meta
import org.bukkit.entity.Player

class MetaEditor(val meta: Meta) {

    /**
     * 是否可编辑
     */
    var editable = true

    /**
     * 枚举类型
     */
    var enumType: Class<*>? = null

    /**
     * 恢复默认
     */
    internal var resetMethod: Function2<Player, EntityInstance, Unit>? = null

    /**
     * 修改数据
     */
    internal var modifyMethod: Function2<Player, EntityInstance, Unit>? = null

    /**
     * 生成展示文本
     */
    internal var displayGenerator: Function2<Player, EntityInstance, Any>? = null

    /**
     * 继承模板
     */
    fun use(parent: MetaEditor): MetaEditor {
        enumType = parent.enumType
        resetMethod = parent.resetMethod
        modifyMethod = parent.modifyMethod
        displayGenerator = parent.displayGenerator
        return this
    }

    fun reset(method: Function2<Player, EntityInstance, Unit>) {
        resetMethod = method
    }

    fun modify(method: Function2<Player, EntityInstance, Unit>) {
        modifyMethod = method
    }

    fun display(method: Function2<Player, EntityInstance, Any>) {
        displayGenerator = method
    }
}