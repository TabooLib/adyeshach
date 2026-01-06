package ink.ptms.adyeshach.core.entity

import org.bukkit.entity.Player
import java.util.*
import java.util.function.Consumer

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ModelEngine
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
     * 模型配置，如果未设置则采用默认配置
     */
    var modelEngineOptions: ModelEngineOptions?

    /**
     * 显示 ModelEngine 模型
     */
    fun showModelEngine(viewer: Player): Boolean

    /**
     * 隐藏 ModelEngine 模型
     */
    fun hideModelEngine(viewer: Player): Boolean

    /**
     * 销毁 ModelEngine 模型
     */
    fun destroyModelEngine()

    /**
     * 刷新 ModelEngine 模型
     */
    fun refreshModelEngine(): Boolean

    /**
     * 更新 ModelEngine 模型的名称
     */
    fun updateModelEngineNameTag()

    /**
     * 受伤效果
     */
    fun hurt()

    /**
     * 从持久化标签还原动画状态
     * 用于在模型重新创建后还原之前的动画状态
     */
    fun restoreAnimationState()

    /**
     * 清除持久化的动画状态
     */
    fun clearAnimationState()

    /**
     * 注册模型创建回调
     */
    fun onModelCreate(handler: Consumer<UUID>)

    /**
     * 注册模型销毁回调
     */
    fun onModelDestroy(handler: Consumer<UUID>)
}