package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.MinecraftWorldAccess
import ink.ptms.adyeshach.impl.DefaultAdyeshachMinecraftAPI
import taboolib.module.nms.nmsProxy

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftWorldAccess
 *
 * @author 坏黑
 * @since 2022/6/28 00:11
 */
class DefaultMinecraftWorldAccess : MinecraftWorldAccess {

    val group = "${DefaultAdyeshachMinecraftAPI::class.java.`package`.name}.nms"

    /** 方块访问接口 **/
    val nmsBlockAccess = nmsProxy<MinecraftWorldAccess>("$group.DefaultMinecraftBlockAccess")

    override fun createBlockAccess(x: Int, z: Int): MinecraftWorldAccess.BlockAccess {
        TODO("Not yet implemented")
    }
}