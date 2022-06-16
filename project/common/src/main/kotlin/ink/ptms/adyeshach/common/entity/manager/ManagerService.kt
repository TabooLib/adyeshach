package ink.ptms.adyeshach.common.entity.manager

import ink.ptms.adyeshach.common.entity.TickService

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.manager.ManagerService
 *
 * @author 坏黑
 * @since 2022/6/16 18:23
 */
@Suppress("SpellCheckingInspection")
interface ManagerService : TickService {

    /**
     * 当管理器启动时
     */
    fun onEnable()

    /**
     * 当管理器关闭时
     */
    fun onDisable()

    /**
     * 当管理器保存时
     */
    fun onSave()
}