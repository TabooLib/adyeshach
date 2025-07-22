package ink.ptms.adyeshach.core.util

import ink.ptms.adyeshach.core.entity.EntityTypes

/**
 * 修复一些特殊实体在客户端的异常视角显示
 */
fun EntityTypes?.fixYaw(yaw: Float): Float {
    return when (this ?: return yaw) {
        EntityTypes.WITHER_SKULL -> yaw + 180
        EntityTypes.MINECART,
        EntityTypes.MINECART_CHEST,
        EntityTypes.MINECART_COMMAND,
        EntityTypes.MINECART_FURNACE,
        EntityTypes.MINECART_HOPPER,
        EntityTypes.MINECART_TNT,
        EntityTypes.MINECART_MOB_SPAWNER -> yaw + 90
        else -> yaw
    }
}