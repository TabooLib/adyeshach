package ink.ptms.adyeshach.core.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.platform.Folia
import java.lang.reflect.Method
import taboolib.platform.util.submit as submitAtScheduler

/**
 * Folia 兼容工具
 *
 * 设计原则：非 Folia 服务端上所有方法与原先的 `submit { }` / 直接执行完全等价；
 * 仅在 Folia 服务端上改为实体调度器或区域调度器。判断只出现在这里，业务代码只替换调用。
 */
object FoliaSupport {

    /** 当前是否运行在 Folia（区域多线程）服务端上 */
    val isFolia: Boolean
        get() = Folia.isFolia

    private val isOwnedEntityMethod: Method? by lazy {
        runCatching { Bukkit::class.java.getMethod("isOwnedByCurrentRegion", Entity::class.java) }.getOrNull()
    }

    private val isOwnedLocationMethod: Method? by lazy {
        runCatching { Bukkit::class.java.getMethod("isOwnedByCurrentRegion", Location::class.java) }.getOrNull()
    }

    private val isOwnedChunkMethod: Method? by lazy {
        runCatching { Bukkit::class.java.getMethod("isOwnedByCurrentRegion", World::class.java, Int::class.java, Int::class.java) }.getOrNull()
    }

    private val teleportAsyncMethod: Method? by lazy {
        runCatching { Entity::class.java.getMethod("teleportAsync", Location::class.java) }.getOrNull()
    }

    /**
     * 当前线程是否可以安全访问该实体
     * Folia：当前线程是否拥有实体所在区域；其他：是否为主线程
     */
    fun isOwnedByCurrentThread(entity: Entity): Boolean {
        if (!isFolia) {
            return Bukkit.isPrimaryThread()
        }
        val method = isOwnedEntityMethod ?: return false
        return runCatching { method.invoke(null, entity) as Boolean }.getOrDefault(false)
    }

    /**
     * 当前线程是否可以安全访问该坐标
     * Folia：当前线程是否拥有坐标所在区域；其他：是否为主线程
     */
    fun isOwnedByCurrentThread(location: Location): Boolean {
        if (!isFolia) {
            return Bukkit.isPrimaryThread()
        }
        val method = isOwnedLocationMethod ?: return false
        return runCatching { method.invoke(null, location) as Boolean }.getOrDefault(false)
    }

    /**
     * 当前线程是否可以安全访问该区块
     * Folia：当前线程是否拥有区块所在区域；其他：是否为主线程
     */
    fun isOwnedByCurrentThread(world: World, chunkX: Int, chunkZ: Int): Boolean {
        if (!isFolia) {
            return Bukkit.isPrimaryThread()
        }
        val method = isOwnedChunkMethod ?: return false
        return runCatching { method.invoke(null, world, chunkX, chunkZ) as Boolean }.getOrDefault(false)
    }

    /**
     * 传送实体
     * Folia 不允许同步传送，此时使用 teleportAsync；其他服务端保持同步传送
     */
    fun teleport(entity: Entity, location: Location) {
        if (isFolia) {
            val method = teleportAsyncMethod
            if (method != null) {
                method.invoke(entity, location)
                return
            }
        }
        entity.teleport(location)
    }
}

/**
 * 在实体所属的线程上调度任务
 * 非 Folia：等价于 `submit(now, delay = delay, period = period)`（主线程）
 * Folia：实体调度器；实体失效（如玩家离线）时任务被丢弃
 */
fun Entity.submitEntity(now: Boolean = false, delay: Long = 0, period: Long = 0, executor: PlatformExecutor.PlatformTask.() -> Unit): PlatformExecutor.PlatformTask {
    return if (Folia.isFolia) {
        submitAtScheduler(now, false, delay, period, true, executor)
    } else {
        submit(now = now, delay = delay, period = period, executor = executor)
    }
}

/**
 * 仅在 Folia 上切换到实体所属线程
 * 非 Folia：在当前线程直接执行（保持原有行为，原先在异步线程中的逻辑仍在异步线程中运行）
 * Folia：当前线程不拥有该实体时调度到实体所属区域线程；实体失效时丢弃
 */
fun Entity.runInEntityRegion(block: () -> Unit) {
    if (!Folia.isFolia || FoliaSupport.isOwnedByCurrentThread(this)) {
        block()
    } else {
        submitEntity { block() }
    }
}

/**
 * 在坐标所属的区域线程上调度任务，坐标为空时退回普通调度
 * 非 Folia：等价于 `submit(delay = delay, period = period)`（主线程）
 */
fun submitInRegion(location: Location?, delay: Long = 0, period: Long = 0, executor: PlatformExecutor.PlatformTask.() -> Unit): PlatformExecutor.PlatformTask {
    return if (Folia.isFolia && location?.world != null) {
        location.submitAtScheduler(false, false, delay, period, true, executor)
    } else {
        submit(delay = delay, period = period, executor = executor)
    }
}

/**
 * 仅在 Folia 上切换到坐标所属的区域线程，坐标为空时直接执行
 * 非 Folia：在当前线程直接执行（保持原有行为）
 */
fun runInRegion(location: Location?, block: () -> Unit) {
    if (!Folia.isFolia || location?.world == null || FoliaSupport.isOwnedByCurrentThread(location)) {
        block()
    } else {
        location.submitAtScheduler(false, false, 0, 0, true) { block() }
    }
}

/**
 * 仅在 Folia 上切换到全局区域线程（例如控制台命令）
 * 非 Folia：在当前线程直接执行（保持原有行为）
 */
fun runInGlobalRegion(block: () -> Unit) {
    if (!Folia.isFolia) {
        block()
    } else {
        submit { block() }
    }
}

/**
 * 兼容 Folia 的传送
 */
fun Entity.teleportCompat(location: Location) {
    FoliaSupport.teleport(this, location)
}
