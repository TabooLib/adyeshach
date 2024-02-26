package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.EventBus
import ink.ptms.adyeshach.core.entity.manager.event.*
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.DefaultEventBus
 *
 * @author 坏黑
 * @since 2022/12/14 20:14
 */
class DefaultEventBus : EventBus {

    val prepareSpawn = ArrayList<Predicate<SpawnEvent>>()
    val prepareDestroy = ArrayList<Predicate<DestroyEvent>>()
    val prepareTicks = LinkedList<Predicate<EntityInstance>>()
    val prepareMoves = LinkedList<Consumer<MoveEvent>>()
    val prepareTeleports = LinkedList<Predicate<TeleportEvent>>()
    val prepareVelocities = LinkedList<Predicate<VelocityEvent>>()
    val prepareMetaUpdates = LinkedList<Predicate<MetaUpdateEvent>>()
    val prepareMaskedMetaGenerates = LinkedList<Predicate<MetaMaskedGenerateEvent>>()
    val prepareNaturalMetaGenerates = LinkedList<Predicate<MetaNaturalGenerateEvent>>()
    val postMetaUpdates = LinkedList<Consumer<MetaUpdateEvent>>()
    val postTeleports = LinkedList<Consumer<TeleportEvent>>()

    fun callSpawn(entity: EntityInstance, viewer: Player): Boolean {
        return prepareSpawn.all { it.test(SpawnEvent(entity, viewer)) }
    }

    fun callDestroy(entity: EntityInstance, viewer: Player): Boolean {
        return prepareDestroy.all { it.test(DestroyEvent(entity, viewer)) }
    }

    fun callTick(entity: EntityInstance): Boolean {
        return prepareTicks.all { it.test(entity) }
    }

    fun callMove(entity: EntityInstance, isMoving: Boolean) {
        prepareMoves.forEach { it.accept(MoveEvent(entity, isMoving)) }
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

    fun postMetaUpdate(event: MetaUpdateEvent) {
        postMetaUpdates.forEach { it.accept(event) }
    }

    fun postTeleport(entity: EntityInstance, location: Location) {
        postTeleports.forEach { it.accept(TeleportEvent(entity, location)) }
    }

    override fun prepareSpawn(callback: Predicate<SpawnEvent>) {
        prepareSpawn.add(callback)
    }

    override fun prepareDestroy(callback: Predicate<DestroyEvent>) {
        prepareDestroy.add(callback)
    }

    override fun prepareTick(callback: Predicate<EntityInstance>) {
        prepareTicks.add(callback)
    }

    override fun prepareMove(callback: Consumer<MoveEvent>) {
        prepareMoves.add(callback)
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

    override fun postMetaUpdate(callback: Consumer<MetaUpdateEvent>) {
        postMetaUpdates.add(callback)
    }

    override fun postTeleport(callback: Consumer<TeleportEvent>) {
        postTeleports.add(callback)
    }
}