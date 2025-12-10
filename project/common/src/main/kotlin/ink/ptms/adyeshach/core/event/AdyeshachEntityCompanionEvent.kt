package ink.ptms.adyeshach.core.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import taboolib.platform.type.BukkitProxyEvent

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.event.AdyeshachEntityCompanionEvent
 *
 * 伴生关系变更事件
 * 当实体的伴生关系发生变化时触发
 *
 * @author 坏黑
 * @since 2024/12/11
 */
class AdyeshachEntityCompanionEvent(
    /** 伴生实体 */
    val entity: EntityInstance,
    /** 新的宿主实体（null 表示解除归属） */
    val host: EntityInstance?,
    /** 之前的宿主实体（null 表示之前无归属） */
    val previousHost: EntityInstance?
) : BukkitProxyEvent()
