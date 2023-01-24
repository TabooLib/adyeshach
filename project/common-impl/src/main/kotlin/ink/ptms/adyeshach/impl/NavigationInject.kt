package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.impl.util.ChunkAccess
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.navigation.BoundingBox

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.NavigationInject
 *
 * @author 坏黑
 * @since 2023/1/24 12:30
 */
object NavigationInject {

    @Awake(LifeCycle.LOAD)
    private fun init() {
        // 替换 Navigation 模块中的 ChunkAccess
//        try {
//            taboolib.module.navigation.ChunkAccess.instance = object : taboolib.module.navigation.ChunkAccess() {
//
//                override fun isChunkLoaded(world: World, chunkX: Int, chunkZ: Int): Boolean {
//                    return ChunkAccess.getChunkAccess(world).isChunkLoaded(chunkX, chunkZ)
//                }
//            }
//        } catch (_: Throwable) {
//        }
    }
}