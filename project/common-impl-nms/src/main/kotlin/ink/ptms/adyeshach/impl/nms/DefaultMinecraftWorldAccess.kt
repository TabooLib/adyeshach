package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.core.MinecraftWorldAccess
import org.bukkit.World
import taboolib.module.nms.nmsProxy

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftWorldAccess
 *
 * @author 坏黑
 * @since 2022/6/28 00:11
 */
class DefaultMinecraftWorldAccess : MinecraftWorldAccess {

    val group: String = DefaultMinecraftWorldAccess::class.java.`package`.name

    /** 方块访问接口 **/
    val nmsBlockAccessClass = nmsProxy<MinecraftWorldAccess.BlockAccess>("$group.DefaultMinecraftBlockAccess")

    override fun createBlockAccess(world: World, x: Int, z: Int): MinecraftWorldAccess.BlockAccess {
        return nmsBlockAccessClass.createCopy(world, x, z)
    }
}