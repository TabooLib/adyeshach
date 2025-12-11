package ink.ptms.adyeshach.core.entity

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.Companionable
 *
 * 伴生关系接口
 * 伴生实体的可见性完全由宿主控制，形成主从绑定关系
 *
 * 特性：
 * - 可见性联动：宿主可见则伴生实体可见，宿主不可见则伴生实体不可见
 * - 生命周期绑定：宿主销毁时，伴生实体也会销毁
 * - 支持链式归属：A → B → C
 * - 伴生实体无法直接操作可见性
 *
 * @author 坏黑
 * @since 2024/12/11
 */
interface Companionable {

    /**
     * 获取直接宿主实体
     */
    fun getHost(): EntityInstance?

    /**
     * 获取根宿主实体（链式归属的最顶层）
     * 如果没有宿主，返回 null
     */
    fun getRootHost(): EntityInstance?

    /**
     * 设置宿主实体（归属给某实体）
     * 传入 null 解除归属
     *
     * @param entity 宿主实体，null 表示解除归属
     */
    fun setHost(entity: EntityInstance?)

    /**
     * 是否有宿主
     */
    fun hasHost(): Boolean

    /**
     * 是否为伴生实体
     */
    fun isCompanion(): Boolean

    /**
     * 获取直接伴生实体列表
     */
    fun getCompanions(): List<EntityInstance>

    /**
     * 获取所有伴生实体（包括嵌套的伴生实体）
     */
    fun getAllCompanions(): List<EntityInstance>

    /**
     * 添加伴生实体
     *
     * @param entity 要添加的伴生实体
     */
    fun addCompanion(vararg entity: EntityInstance)

    /**
     * 移除伴生实体
     *
     * @param entity 要移除的伴生实体
     */
    fun removeCompanion(vararg entity: EntityInstance)

    /**
     * 清空所有伴生实体
     */
    fun clearCompanions()
}
