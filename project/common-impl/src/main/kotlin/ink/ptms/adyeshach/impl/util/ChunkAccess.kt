package ink.ptms.adyeshach.impl.util

import com.github.benmanes.caffeine.cache.Caffeine
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftWorldAccess
import ink.ptms.adyeshach.core.util.ifloor
import ink.ptms.adyeshach.minecraft.ChunkPos
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.world.WorldUnloadEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
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