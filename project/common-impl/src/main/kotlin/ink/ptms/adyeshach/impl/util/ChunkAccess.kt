package ink.ptms.adyeshach.impl.util

import com.github.benmanes.caffeine.cache.Caffeine
import ink.ptms.adyeshach.minecraft.ChunkPos
import org.bukkit.World
import java.util.concurrent.TimeUnit

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

    fun isChunkLoaded(x: Int, z: Int): Boolean {
        return loadingCache.get(ChunkPos.asLong(x, z))!!
    }
}