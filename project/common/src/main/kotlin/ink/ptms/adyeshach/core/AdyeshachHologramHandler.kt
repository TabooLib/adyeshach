package ink.ptms.adyeshach.core

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachHologramHandler
 *
 * @author 坏黑
 * @since 2022/6/16 17:31
 */
interface AdyeshachHologramHandler {

    /**
     * 创建一个新的文本条目
     */
    fun createHologramItem(text: String, space: Double = 0.3): AdyeshachHologram.ItemByText

    /**
     * 创建一个新的物品条目
     */
    fun createHologramItem(itemStack: ItemStack, space: Double = 0.4): AdyeshachHologram.ItemByItemStack

    /**
     * 创建全服可见的混合全息
     */
    fun createHologram(location: Location, content: List<Any>, isolate: Boolean = false): AdyeshachHologram

    /**
     * 创建玩家私有的混合全息
     */
    fun createHologram(player: Player, location: Location, content: List<Any>, isolate: Boolean = false): AdyeshachHologram

    /**
     * 创建全息通告（以全息形式发送位于世界中的提示信息）
     *
     * @param location 坐标
     * @param message 内容
     * @param stay 停留时间
     */
    fun sendHologramMessage(location: Location, message: List<String>, stay: Long = 40L)

    /**
     * 创建全息通告（以全息形式发送位于世界中的提示信息）
     *
     * @param player 玩家
     * @param location 坐标
     * @param message 内容
     * @param stay 停留时间
     */
    fun sendHologramMessage(player: Player, location: Location, message: List<String>, stay: Long = 40L)
}