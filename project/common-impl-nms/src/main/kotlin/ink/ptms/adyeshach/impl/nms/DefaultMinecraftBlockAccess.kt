package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.core.MinecraftWorldAccess
import ink.ptms.adyeshach.impl.DefaultAdyeshachMinecraftAPI
import net.minecraft.world.level.chunk.Chunk
import net.minecraft.world.level.chunk.ChunkStatus
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.type.Slab
import taboolib.module.nms.MinecraftVersion

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftBlockAccess
 *
 * @author 坏黑
 * @since 2022/10/19 09:23
 */
@Suppress("CAST_NEVER_SUCCEEDS")
class DefaultMinecraftBlockAccess(val world: World?, override val x: Int, override val z: Int) : MinecraftWorldAccess.BlockAccess {

    constructor() : this(null, 0, 0)

    val major = MinecraftVersion.major
    val obcChunk = world?.getChunkAt(x, z) as? CraftChunk19

    // 1.19.4 (最新版有改动)
    val nmsChunk: Any? = try {
        obcChunk?.getHandle(ChunkStatus.FULL)
    } catch (_: Throwable) {
        (obcChunk as org.bukkit.craftbukkit.v1_19_R2.CraftChunk?)?.handle
    }

    override fun getBlockType(x: Int, y: Int, z: Int): Material {
        val block: Any = when (major) {
            // 1.9, 1.10, 1.11, 1.12, 1.13
            1, 2, 3, 4, 5 -> (nmsChunk as NMS9Chunk).getBlockData(NMS9BlockPosition(x, y, z)).block
            // 1.14, 1.15, 1.16, 1.17
            6, 7, 8 -> (nmsChunk as NMS16IBlockAccess).getType(NMS16BlockPosition(x, y, z)).block
            // 1.17
            // 这个版本的命名与 1.16 相同，但是类型不同
            9 -> ((nmsChunk as NMS16IBlockAccess).getType(NMS16BlockPosition(x, y, z)) as NMSBlockData).block
            // 1.18, 1.19
            10, 11 -> ((nmsChunk as NMSIBlockAccess).getBlockState(NMSBlockPosition(x, y, z)) as NMSBlockData).block
            // 不支持
            else -> error("Unsupported version: $major")
        }
        // 如果是空气则跳过检索
        if (block == NMSBlocks.AIR) {
            return Material.AIR
        }
        return CraftMagicNumbers19.getMaterial(block as NMSBlock)
    }

    override fun getBlockHeight(x: Int, y: Int, z: Int): Double {
        return getBlockTypeAndHeight(x, y, z).second
    }

    override fun getHighestBlock(x: Int, y: Int, z: Int): Double {
        var cy = y
        while (cy > 0) {
            val typeAndHeight = getBlockTypeAndHeight(x, cy, z)
            if (typeAndHeight.first.isSolid) {
                return cy + typeAndHeight.second
            }
            cy--
        }
        return cy.toDouble()
    }

    override fun getBlockTypeAndHeight(x: Int, y: Int, z: Int): Pair<Material, Double> {
        val blockType = getBlockType(x, y, z)
        // 获取材质名称
        val name = blockType.name
        // 对半砖进行特殊处理
        return blockType to if (name.endsWith("_SLAB") || name.endsWith("_SLAB2")) {
            when (major) {
                // 1.9, 1.10, 1.11, 1.12
                1, 2, 3, 4 -> getBlockHeightLegacy(obcChunk!!.getBlock(x, y, z))
                // 1.13, 1.14, 1.15, 1.16, 1.17, 1.18, 1.19
                5, 6, 7, 8, 9, 10, 11 -> {
                    val slab = world!!.getBlockAt(x, y, z).blockData as Slab
                    if (slab.type == Slab.Type.TOP || slab.type == Slab.Type.DOUBLE) 1.0 else 0.5
                }
                // 不支持
                else -> error("Unsupported version: $major")
            }
        } else {
            DefaultAdyeshachMinecraftAPI.blockHeightMap[blockType.name] ?: 0.0
        }
    }

    override fun createCopy(world: World, x: Int, z: Int): MinecraftWorldAccess.BlockAccess {
        return DefaultMinecraftBlockAccess(world, x, z)
    }

    fun getBlockHeightLegacy(block: Block): Double {
        return when (major) {
            // 1.12
            4 -> {
                val p = NMS12BlockPosition(block.x, block.y, block.z)
                val b = (block.world as CraftWorld12).handle.getType(p)
                b.d((block.world as CraftWorld12).handle, p)?.e ?: 0.0
            }
            // 1.11
            3 -> {
                val p = NMS11BlockPosition(block.x, block.y, block.z)
                val b = (block.world as CraftWorld11).handle.getType(p)
                b.c((block.world as CraftWorld11).handle, p)?.e ?: 0.0
            }
            // 1.9, 1.10
            else -> {
                val p = NMS9BlockPosition(block.x, block.y, block.z)
                val b = (block.world as CraftWorld9).handle.getType(p)
                b.c((block.world as CraftWorld9).handle, p)?.e ?: 0.0
            }
        }
    }
}