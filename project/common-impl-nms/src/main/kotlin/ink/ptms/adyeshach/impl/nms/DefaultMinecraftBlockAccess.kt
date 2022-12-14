package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.core.MinecraftWorldAccess
import ink.ptms.adyeshach.impl.DefaultAdyeshachMinecraftAPI
import org.bukkit.Material
import org.bukkit.World
import taboolib.module.nms.MinecraftVersion

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftBlockAccess
 *
 * @author 坏黑
 * @since 2022/10/19 09:23
 */
class DefaultMinecraftBlockAccess(val world: World?, val x: Int, val z: Int) : MinecraftWorldAccess.BlockAccess {

    constructor() : this(null, 0, 0)

    val major = MinecraftVersion.major
    val obcChunk = world?.getChunkAt(x, z) as? CraftChunk19
    val nmsChunk = obcChunk?.handle

    override fun getBlockType(x: Int, y: Int, z: Int): Material {
        val block: Any = when (major) {
            // 1.9, 1.10, 1.11, 1.12, 1.13, 1.14, 1.15, 1.16
            1, 2, 3, 4, 5, 6, 7, 8 -> (nmsChunk as NMS9IBlockAccess).getType(NMS9BlockPosition(x, y, z)).block
            // 1.17, 1.18, 1.19
            9, 10, 11 -> ((nmsChunk as NMSIBlockAccess).getBlockState(NMSBlockPosition(x, y, z)) as NMSBlockData).block
            else -> error("Unsupported version: $major")
        }
        // 如果是空气则跳过检索
        if (block == NMSBlocks.AIR) {
            return Material.AIR
        }
        return CraftMagicNumbers19.getMaterial(block as NMSBlock)
    }

    override fun getBlockHeight(x: Int, y: Int, z: Int): Double {
        return DefaultAdyeshachMinecraftAPI.blockHeightMap[getBlockType(x, y, z).name] ?: 0.0
    }

    override fun getHighestBlock(x: Int, y: Int, z: Int): Int {
        var cy = y
        while (cy > 0) {
            if (getBlockType(x, cy, z).isSolid) {
                return cy
            }
            cy--
        }
        return cy
    }

    override fun createCopy(world: World, x: Int, z: Int): MinecraftWorldAccess.BlockAccess {
        return DefaultMinecraftBlockAccess(world, x, z)
    }
}