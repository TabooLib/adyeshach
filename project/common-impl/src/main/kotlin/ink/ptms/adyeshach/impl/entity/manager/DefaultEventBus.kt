package ink.ptms.adyeshach.impl.entity.manager

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.EventBus
import ink.ptms.adyeshach.core.entity.manager.event.*
import org.bukkit.Location
import org.bukkit.util.Vector
import java.util.*
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.manager.DefaultEventBus
 *
 * @author 坏黑
 * @since 2022/12/14 20:14
 */
class DefaultEventBus : EventBus {

    val prepareTicks = LinkedList<Predicate<EntityInstance>>()
    val prepareTeleports = LinkedList<Predicate<TeleportEvent>>()
    val prepareVelocities = LinkedList<Predicate<VelocityEvent>>()
    val prepareMetaUpdates = LinkedList<Predicate<MetaUpdateEvent>>()
    val prepareMaskedMetaGenerates = LinkedList<Predicate<MetaMaskedGenerateEvent>>()
    val prepareNaturalMetaGenerates = LinkedList<Predicate<MetaNaturalGenerateEvent>>()

    fun callTick(entity: EntityInstance): Boolean {
        return prepareTicks.all { it.test(entity) }
    }

    fun callTeleport(entity: EntityInstance, location: Location): Boolean {
        return prepareTeleports.all { it.test(TeleportEvent(entity, location)) }
    }

    fun callVelocity(entity: EntityInstance, vector: Vector): Boolean {
        return prepareVelocities.all { it.test(VelocityEvent(entity, vector)) }
    }

    fun callMetaUpdate(event: MetaUpdateEvent): Boolean {
        return prepareMetaUpdates.all { it.test(event) }
    }

    fun callMaskedMetaGenerate(event: MetaMaskedGenerateEvent): Boolean {
        return prepareMaskedMetaGenerates.all { it.test(event) }
    }

    fun callNaturalMetaGenerate(event: MetaNaturalGenerateEvent): Boolean {
        return prepareNaturalMetaGenerates.all { it.test(event) }
    }

    override fun prepareTick(callback: Predicate<EntityInstance>) {
        prepareTicks.add(callback)
    }

    override fun prepareTeleport(callback: Predicate<TeleportEvent>) {
        prepareTeleports.add(callback)
    }

    override fun prepareVelocity(callback: Predicate<VelocityEvent>) {
        prepareVelocities.add(callback)
    }

    override fun prepareMetaUpdate(callback: Predicate<MetaUpdateEvent>) {
        prepareMetaUpdates.add(callback)
    }

    override fun prepareMaskedMetaGenerate(callback: Predicate<MetaMaskedGenerateEvent>) {
        prepareMaskedMetaGenerates.add(callback)
    }

    override fun prepareNaturalMetaGenerate(callback: Predicate<MetaNaturalGenerateEvent>) {
        prepareNaturalMetaGenerates.add(callback)
    }
}