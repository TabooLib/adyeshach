package ink.ptms.adyeshach.common.api

import org.bukkit.Location

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.AdyeshachHologram
 *
 * @author 坏黑
 * @since 2022/6/16 18:12
 */
interface AdyeshachHologram {

    /**
     * 传送到某处
     */
    fun teleport(location: Location)

    /**
     * 更新内容
     */
    fun update(content: List<String>)

    /**
     * 删除全息
     */
    fun delete()
}