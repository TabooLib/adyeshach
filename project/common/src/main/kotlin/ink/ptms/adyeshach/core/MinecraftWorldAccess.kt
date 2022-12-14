package ink.ptms.adyeshach.core

import org.bukkit.block.Block

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.MinecraftWorldAccess
 *
 * @author 坏黑
 * @since 2022/6/16 11:45
 */
interface MinecraftWorldAccess {

    /**
     * 在区块上创建内部方块信息访问接口
     */
    fun createBlockAccess(x: Int, z: Int): BlockAccess

    /**
     * 内部方块信息访问接口
     */
    interface BlockAccess: Access {

        /**
         * 获取方块
         */
        fun getBlock(x: Int, y: Int, z: Int): Block

        /**
         * 获取方块的碰撞箱宽度
         */
        fun getBlockWidth(x: Int, y: Int, z: Int): Double

        /**
         * 获取方块的碰撞箱高度
         */
        fun getBlockHeight(x: Int, y: Int, z: Int): Double

        /**
         * 从给定坐标向下获取距离最近的固体方块
         */
        fun getHighestBlock(x: Int, y: Int, z: Int): Int
    }

    interface Access {

        /**
         * 清空缓存
         */
        fun clear()
    }
}