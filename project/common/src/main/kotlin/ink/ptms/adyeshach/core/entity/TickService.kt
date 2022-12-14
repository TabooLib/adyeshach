package ink.ptms.adyeshach.core.entity

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.TickService
 *
 * @author 坏黑
 * @since 2022/6/16 00:16
 */
interface TickService {

    /**
     * 根据服务器运行状态平均 50 毫秒自动运行一次该方法
     */
    fun onTick()
}