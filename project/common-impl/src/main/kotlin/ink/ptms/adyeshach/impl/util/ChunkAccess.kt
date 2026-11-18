package ink.ptms.adyeshach.impl.util

import com.github.benmanes.caffeine.cache.Caffeine
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftWorldAccess
import ink.ptms.adyeshach.core.util.FoliaSupport
import ink.ptms.adyeshach.core.util.ifloor
import ink.ptms.adyeshach.minecraft.ChunkPos
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.world.WorldUnloadEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.runTask
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.util.ChunkAccess
 *
 * @author 坏黑
 * @since 2022/6/28 14:29
 */
class ChunkAccess(val world: World) {

    val loadingCache = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.SECONDS)
        .build<Long, Boolean> { world.isChunkLoaded(ChunkPos.getX(it), ChunkPos.getZ(it)) }

    /** Folia：正在区块所属线程中预热的区块 */
    private val warmingChunks: MutableSet<Long> = ConcurrentHashMap.newKeySet()

    val blockAccessCache = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .build<Long, MinecraftWorldAccess.BlockAccess>()

    fun isChunkLoaded(chunkX: Int, chunkZ: Int): Boolean {
        return loadingCache.get(ChunkPos.asLong(chunkX, chunkZ))!!
    }

    fun getBlockAccess(chunkX: Int, chunkZ: Int): MinecraftWorldAccess.BlockAccess {
        return blockAccessCache.get(ChunkPos.asLong(chunkX, chunkZ)) {
            Adyeshach.api().getMinecraftAPI().getWorldAccess().createBlockAccess(world, chunkX, chunkZ)
        }!!
    }

    /**
     * 获取区块方块访问接口
     * 非 Folia：等同于 [getBlockAccess]
     * Folia：仅在缓存命中或当前线程拥有该区块时返回；否则投递到区块所属线程预热缓存并返回 null，
     * 避免在不拥有区块的线程上加载区块。
     */
    fun getBlockAccessOrNull(chunkX: Int, chunkZ: Int): MinecraftWorldAccess.BlockAccess? {
        if (!FoliaSupport.isFolia) {
            return getBlockAccess(chunkX, chunkZ)
        }
        val key = ChunkPos.asLong(chunkX, chunkZ)
        blockAccessCache.getIfPresent(key)?.let { return it }
        if (FoliaSupport.isOwnedByCurrentThread(world, chunkX, chunkZ)) {
            return getBlockAccess(chunkX, chunkZ)
        }
        if (warmingChunks.add(key)) {
            try {
                Location(world, (chunkX shl 4) + 8.0, 64.0, (chunkZ shl 4) + 8.0).runTask(Runnable {
                    try {
                        getBlockAccess(chunkX, chunkZ)
                    } catch (ex: Throwable) {
                        ex.printStackTrace()
                    } finally {
                        warmingChunks.remove(key)
                    }
                })
            } catch (ex: Throwable) {
                warmingChunks.remove(key)
                ex.printStackTrace()
            }
        }
        return null
    }

    /**
     * 获取方块类型与高度，Folia 上区块暂不可访问时返回 null
     */
    fun getBlockTypeAndHeightOrNull(x: Double, y: Double, z: Double): Pair<Material, Double>? {
        return getBlockAccessOrNullByPos(x, z)?.getBlockTypeAndHeight(ifloor(x), ifloor(y), ifloor(z))
    }

    /**
     * 获取最高方块，Folia 上区块暂不可访问时返回 null
     */
    fun getBlockHighestOrNull(x: Double, y: Double, z: Double): Double? {
        return getBlockAccessOrNullByPos(x, z)?.getHighestBlock(ifloor(x), ifloor(y), ifloor(z))
    }

    private fun getBlockAccessOrNullByPos(x: Double, z: Double): MinecraftWorldAccess.BlockAccess? {
        return getBlockAccessOrNull(floor(x).toInt() shr 4, floor(z).roundToInt() shr 4)
    }

    fun getBlockAccessByPos(x: Double, z: Double): MinecraftWorldAccess.BlockAccess {
        return getBlockAccess(floor(x).toInt() shr 4, floor(z).roundToInt() shr 4)
    }

    fun getBlockTypeAndHeight(x: Double, y: Double, z: Double): Pair<Material, Double> {
        return getBlockAccessByPos(x, z).getBlockTypeAndHeight(ifloor(x), ifloor(y), ifloor(z))
    }

    fun getBlockHighest(x: Double, y: Double, z: Double): Double {
        return getBlockAccessByPos(x, z).getHighestBlock(ifloor(x), ifloor(y), ifloor(z))
    }

    companion object {

        private val chunkAccess: MutableMap<String, ChunkAccess> = ConcurrentHashMap()

        fun getChunkAccess(world: World): ChunkAccess {
            return if (chunkAccess.containsKey(world.name)) {
                chunkAccess[world.name]!!
            } else {
                val access = ChunkAccess(world)
                chunkAccess[world.name] = access
                access
            }
        }

        @SubscribeEvent
        private fun onUnload(e: WorldUnloadEvent) {
            submitAsync { chunkAccess.remove(e.world.name) }
        }
    }
}