@file:Suppress("UNCHECKED_CAST")

package ink.ptms.adyeshach.core.entity.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyEntity
import ink.ptms.adyeshach.core.entity.type.errorBy
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 * [Manager#create] 衍生方法
 *
 * 用法：
 * ```kotlin
 * manager.create(location, AdyHuman::class.java) {
 *     it.setName("test")
 * }
 * ```
 */
fun <T : AdyEntity> Manager.create(location: Location, entityType: Class<out T>, callback: Consumer<T> = Consumer { }): T {
    val type = Adyeshach.api().getEntityTypeRegistry().getEntityTypeFromAdyClass(entityType) ?: errorBy("error-entity-type-not-found", entityType.name)
    return create(type, location) { callback.accept(it as T) } as T
}

/**
 * [Manager#create] 衍生方法
 *
 * 用法：
 * ```kotlin
 * manager.create<AdyHuman>(location) {
 *     it.setName("test")
 * }
 * ```
 */
inline fun <reified T : AdyEntity> Manager.create(location: Location, callback: Consumer<T> = Consumer { }): T {
    return create(location, T::class.java, callback)
}

/**
 * [Manager#create] 衍生方法
 *
 * 用法：
 * ```kotlin
 * manager.create(location, players, AdyHuman::class.java) {
 *     it.setName("test")
 * }
 * ```
 */
fun <T : AdyEntity> Manager.create(location: Location, player: List<Player>, entityType: Class<out T>, callback: Consumer<T> = Consumer { }): T {
    val type = Adyeshach.api().getEntityTypeRegistry().getEntityTypeFromAdyClass(entityType) ?: errorBy("error-entity-type-not-found", entityType.name)
    return create(type, location, player) { callback.accept(it as T) } as T
}

/**
 * [Manager#create] 衍生方法
 *
 * 用法：
 * ```kotlin
 * manager.create<AdyHuman>(location, players) {
 *     it.setName("test")
 * }
 * ```
 */
inline fun <reified T : AdyEntity> Manager.create(location: Location, player: List<Player>, callback: Consumer<T> = Consumer { }): T {
    return create(location, player, T::class.java, callback)
}

/**
 * [Manager#create] 衍生方法
 *
 * 用法：
 * ```kotlin
 * world.spawn(location, AdyHuman::class.java) {
 *     it.setName("test")
 * }
 * ```
 */
fun <T : AdyEntity> World.spawn(location: Location, entityType: Class<out T>, type: ManagerType = ManagerType.TEMPORARY, callback: Consumer<T> = Consumer { }): T {
    return Adyeshach.api().getPublicEntityManager(type).create(location, entityType, callback)
}

/**
 * [Manager#create] 衍生方法
 *
 * 用法：
 * ```kotlin
 * world.spawn<AdyHuman>(location) {
 *     it.setName("test")
 * }
 * ```
 */
inline fun <reified T : AdyEntity> World.spawn(location: Location, type: ManagerType = ManagerType.TEMPORARY, callback: Consumer<T> = Consumer { }): T {
    return Adyeshach.api().getPublicEntityManager(type).create(location, T::class.java, callback)
}

/**
 * [Manager#create] 衍生方法
 *
 * 用法：
 * ```kotlin
 * world.spawnEntity(location, EntityTypes.PLAYER) {
 *     it.setName("test")
 * }
 * ```
 */
fun World.spawnEntity(location: Location, entityType: EntityTypes, type: ManagerType = ManagerType.TEMPORARY, callback: Consumer<EntityInstance> = Consumer { }): EntityInstance {
    return Adyeshach.api().getPublicEntityManager(type).create(entityType, location, callback) as AdyEntity
}