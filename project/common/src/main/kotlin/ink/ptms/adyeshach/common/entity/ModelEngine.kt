package ink.ptms.adyeshach.common.entity

import org.bukkit.entity.Player
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.ModelEngine
 *
 * @author 坏黑
 * @since 2022/6/16 00:00
 */
interface ModelEngine {

    /**
     * 模型名称，基于 ModelEngine 插件
     */
    var modelEngineName: String

    /**
     * 模型序号，基于 ModelEngine 插件
     */
    var modelEngineUniqueId: UUID?

    /**
     * 显示 ModelEngine 模型
     */
    fun showModelEngine(viewer: Player): Boolean

    /**
     * 隐藏 ModelEngine 模型
     */
    fun hideModelEngine(viewer: Player): Boolean

    /**
     * 刷新 ModelEngine 模型
     */
    fun refreshModelEngine(): Boolean

    /**
     * 更新 ModelEngine 模型的名称
     */
    fun updateModelEngineNameTag()
}