package ink.ptms.adyeshach.core.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 实体头衔显示事件
 * 当实体通过 TraitTitle 为特定玩家创建或更新头衔全息时触发。
 * 可通过取消事件拦截默认全息渲染行为并由外部接管，也可直接修改展示文本或偏移高度。
 *
 * @author sky
 * @property entity 目标实体对象
 * @property viewer 观察者玩家
 * @property title 头衔文本内容
 * @property height 头衔偏移高度
 * @property isUpdate 是否为定时刷新事件
 */
class AdyeshachEntityTitleEvent(
    /** 目标实体对象 */
    val entity: EntityInstance,
    /** 观察者玩家 */
    val viewer: Player,
    /** 头衔文本内容（支持接管修改） */
    var title: List<String>,
    /** 头衔偏移高度（支持接管修改） */
    var height: Double,
    /** 是否为定时刷新事件 */
    val isUpdate: Boolean = false,
) : BukkitProxyEvent()
