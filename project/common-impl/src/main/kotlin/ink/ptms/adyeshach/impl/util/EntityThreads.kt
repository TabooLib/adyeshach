package ink.ptms.adyeshach.impl.util

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.PlayerManager
import ink.ptms.adyeshach.core.util.FoliaSupport
import ink.ptms.adyeshach.core.util.runInEntityRegion
import ink.ptms.adyeshach.core.util.runInRegion
import ink.ptms.adyeshach.core.util.submitEntity
import ink.ptms.adyeshach.core.util.submitInRegion
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.platform.util.runTask
import kotlin.math.floor

/**
 * Adyeshach 实体在 Folia 上的线程归属
 *
 * - 私有实体（私有管理器且仅被所有者观察）：所有者（观察者）的实体线程
 * - 公有实体：实体所在区块的区域线程
 * - 私有多人观察实体（观察者包含所有者以外的玩家）：实体所在区块的区域线程
 *
 * 非 Folia 服务端上所有方法与原有逻辑完全等价（直接在当前线程执行或使用原 submit）。
 */
object EntityThreads {

    /**
     * 获取实体的所属观察者
     * 仅当实体位于私有管理器且只被所有者观察时返回所有者，否则返回 null（使用区块线程）
     */
    fun ownerOf(entity: EntityInstance): Player? {
        val manager = entity.manager as? PlayerManager ?: return null
        val owner = manager.owner
        val ownerName = owner.name
        // 私有多人观察：存在所有者以外的观察者
        if (entity.viewPlayers.viewers.any { it != ownerName }) {
            return null
        }
        return owner
    }

    /**
     * 当前线程是否为实体所属线程
     * 非 Folia 总是返回 true（保持原有行为，不做任何切换）
     */
    fun isOnEntityThread(entity: EntityInstance): Boolean {
        if (!FoliaSupport.isFolia) {
            return true
        }
        val owner = ownerOf(entity)
        if (owner != null) {
            return FoliaSupport.isOwnedByCurrentThread(owner)
        }
        val chunk = chunkOf(entity)
        return FoliaSupport.isOwnedByCurrentThread(chunk.world, chunk.x, chunk.z)
    }

    /**
     * 获取实体当前所在区块
     */
    fun chunkOf(entity: EntityInstance): ChunkKey {
        if (entity is DefaultEntityInstance) {
            val position = entity.clientPosition
            return ChunkKey(position.world, floor(position.x).toInt() shr 4, floor(position.z).toInt() shr 4)
        }
        val location = entity.getLocation()
        return ChunkKey(location.world!!, location.blockX shr 4, location.blockZ shr 4)
    }

    /**
     * 批量在实体所属线程上执行
     * 非 Folia：按原顺序在当前线程直接执行
     * Folia：按所属线程（所有者 / 区块）分组后分别投递
     */
    fun <T> forEach(items: Iterable<T>, entityOf: (T) -> EntityInstance, action: (T) -> Unit) {
        if (!FoliaSupport.isFolia) {
            items.forEach(action)
            return
        }
        val batch = Batch<T>()
        items.forEach { batch.add(entityOf(it), it) }
        batch.dispatch { list -> list.forEach { runSafely { action(it) } } }
    }

    /**
     * 批量在实体所属线程上执行
     */
    fun forEach(entities: Iterable<EntityInstance>, action: (EntityInstance) -> Unit) {
        forEach(entities, { it }, action)
    }

    inline fun runSafely(block: () -> Unit) {
        try {
            block()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    /**
     * 区块坐标
     */
    data class ChunkKey(val world: World, val x: Int, val z: Int) {

        fun toLocation(): Location {
            return Location(world, (x shl 4) + 8.0, 64.0, (z shl 4) + 8.0)
        }
    }

    /**
     * 按所属线程分组的任务批次（仅用于 Folia）
     */
    class Batch<T> {

        private val byOwner = LinkedHashMap<Player, MutableList<T>>()
        private val byChunk = LinkedHashMap<ChunkKey, MutableList<T>>()

        fun add(entity: EntityInstance, item: T) {
            val owner = ownerOf(entity)
            if (owner != null) {
                byOwner.getOrPut(owner) { ArrayList() } += item
            } else {
                byChunk.getOrPut(chunkOf(entity)) { ArrayList() } += item
            }
        }

        fun addToOwner(owner: Player, item: T) {
            byOwner.getOrPut(owner) { ArrayList() } += item
        }

        fun addToChunk(chunk: ChunkKey, item: T) {
            byChunk.getOrPut(chunk) { ArrayList() } += item
        }

        fun isEmpty(): Boolean {
            return byOwner.isEmpty() && byChunk.isEmpty()
        }

        /**
         * 投递到各自的线程
         * 实体调度器在玩家离线后会丢弃任务，此时 [onDropped] 被调用
         */
        fun dispatch(onDropped: (List<T>) -> Unit = {}, action: (List<T>) -> Unit) {
            byOwner.forEach { (owner, list) ->
                if (!owner.isOnline) {
                    onDropped(list)
                    return@forEach
                }
                try {
                    owner.runTask(Runnable { action(list) })
                } catch (ex: Throwable) {
                    onDropped(list)
                    ex.printStackTrace()
                }
            }
            byChunk.forEach { (chunk, list) ->
                try {
                    chunk.toLocation().runTask(Runnable { action(list) })
                } catch (ex: Throwable) {
                    onDropped(list)
                    ex.printStackTrace()
                }
            }
        }
    }
}

/**
 * 在实体所属线程上执行
 * 非 Folia：在当前线程直接执行（保持原有行为）
 * Folia：私有实体切换到所有者线程，公有实体与私有多人观察实体切换到所在区块线程；已在该线程时直接执行
 */
fun EntityInstance.runOnEntityThread(block: () -> Unit) {
    if (!FoliaSupport.isFolia) {
        block()
        return
    }
    val owner = EntityThreads.ownerOf(this)
    if (owner != null) {
        owner.runInEntityRegion(block)
    } else {
        runInRegion(EntityThreads.chunkOf(this).toLocation(), block)
    }
}

/**
 * 在实体所属线程上延迟执行
 * 非 Folia：等价于 `submit(delay = delay)`（主线程）
 * Folia：私有实体使用所有者的实体调度器，公有实体与私有多人观察实体使用所在区块的区域调度器
 */
fun EntityInstance.submitOnEntityThread(delay: Long = 0, executor: PlatformExecutor.PlatformTask.() -> Unit): PlatformExecutor.PlatformTask {
    if (!FoliaSupport.isFolia) {
        return submit(delay = delay, executor = executor)
    }
    val owner = EntityThreads.ownerOf(this)
    return if (owner != null) {
        owner.submitEntity(delay = delay, executor = executor)
    } else {
        submitInRegion(EntityThreads.chunkOf(this).toLocation(), delay = delay, executor = executor)
    }
}
