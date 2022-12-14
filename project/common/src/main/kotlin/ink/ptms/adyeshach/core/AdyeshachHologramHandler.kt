package ink.ptms.adyeshach.core

import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyEntity
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer
import java.util.function.Function

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
    fun createHologramItem(text: ItemStack, space: Double = 0.3): AdyeshachHologram.ItemByItemStack

    /**
     * 创建一个新的实体条目
     */
    fun <T : AdyEntity> createHologramItem(type: EntityTypes, space: Double = 0.3, text: Consumer<T>): AdyeshachHologram.ItemByEntity<T>

    /**
     * 创建全服玩家可见的混合全息
     */
    fun createHologram(location: Location, content: List<AdyeshachHologram.Item>, isolate: Boolean = false): AdyeshachHologram

    /**
     * 创建全服玩家可见的文本全息
     */
    fun createHologramByText(location: Location, content: List<String>, isolate: Boolean = false): AdyeshachHologram

    /**
     * 创建玩家私有的混合全息
     */
    fun createHologram(player: Player, location: Location, content: List<AdyeshachHologram.Item>, isolate: Boolean = false): AdyeshachHologram

    /**
     * 创建玩家私有的文本全息
     */
    fun createHologramByText(player: Player, location: Location, content: List<String>, isolate: Boolean = false): AdyeshachHologram

    /**
     * 创建全息通告（以全息形式发送位于世界中的提示信息）
     *
     * @param location 坐标
     * @param message 内容
     * @param stay 停留时间
     * @param transfer 内容转换回调函数
     */
    fun createHologramMessage(location: Location, message: List<String>, stay: Long = 40L, transfer: Function<String, String> = Function { it })

    /**
     * 创建全息通告（以全息形式发送位于世界中的提示信息）
     *
     * @param player 玩家
     * @param location 坐标
     * @param message 内容
     * @param stay 停留时间
     * @param transfer 内容转换回调函数
     */
    fun createHologramMessage(player: Player, location: Location, message: List<String>, stay: Long = 40L, transfer: Function<String, String> = Function { it })
}