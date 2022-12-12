package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.MinecraftWorldAccess
import org.bukkit.block.Block

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftBlockAccess
 *
 * @author 坏黑
 * @since 2022/10/19 09:23
 */
class DefaultMinecraftBlockAccess(val x: Int, val z: Int) : MinecraftWorldAccess.BlockAccess {

    override fun getBlock(x: Int, y: Int, z: Int): Block {
        TODO("Not yet implemented")
    }

    override fun getBlockWidth(x: Int, y: Int, z: Int): Double {
        TODO("Not yet implemented")
    }

    override fun getBlockHeight(x: Int, y: Int, z: Int): Double {
        TODO("Not yet implemented")
    }

    override fun getHighestBlock(x: Int, y: Int, z: Int): Int {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}