package ink.ptms.adyeshach.core.entity.manager

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.event.*
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.manager.EventBus
 *
 * @author 坏黑
 * @since 2022/12/14 20:02
 */
interface EventBus {

    /**
     * 在 onTick 之前插入一段回调函数
     */
    fun prepareTick(callback: Predicate<EntityInstance>)

    /**
     * 在 teleport 之前插入一段回调函数
     */
    fun prepareTeleport(callback: Predicate<TeleportEvent>)

    /**
     * 在 setVelocity 之前插入一段回调函数
     */
    fun prepareVelocity(callback: Predicate<VelocityEvent>)

    /**
     * 在 Meta 更新之前插入一段回调函数
     */
    fun prepareMetaUpdate(callback: Predicate<MetaUpdateEvent>)

    /**
     * 在 MetaMasked 生成之前插入一段回调函数
     */
    fun prepareMaskedMetaGenerate(callback: Predicate<MetaMaskedGenerateEvent>)

    /**
     * 在 MetaNatural 生之前后插入一段回调函数
     */
    fun prepareNaturalMetaGenerate(callback: Predicate<MetaNaturalGenerateEvent>)
}