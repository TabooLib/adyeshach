package ink.ptms.adyeshach.core

import org.bukkit.Material
import org.bukkit.World

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
    fun createBlockAccess(world: World, x: Int, z: Int): BlockAccess

    /**
     * 内部方块信息访问接口
     */
    interface BlockAccess {

        /**
         * 复制一个接口
         */
        fun createCopy(world: World, x: Int, z: Int): BlockAccess

        /**
         * 获取方块
         */
        fun getBlockType(x: Int, y: Int, z: Int): Material

        /**
         * 获取方块的碰撞箱高度
         */
        fun getBlockHeight(x: Int, y: Int, z: Int): Double

        /**
         * 从给定坐标向下获取距离最近的固体方块
         */
        fun getHighestBlock(x: Int, y: Int, z: Int): Int
    }
}