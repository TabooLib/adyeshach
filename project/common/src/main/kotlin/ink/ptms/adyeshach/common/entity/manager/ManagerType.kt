package ink.ptms.adyeshach.common.entity.manager

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.manager.ManagerType
 *
 * @author 坏黑
 * @since 2022/12/13 21:33
 */
enum class ManagerType {

    /**
     * 持久化
     * 保存实体数据
     */
    PERSISTENT,

    /**
     * 临时
     * 不保存实体数据
     */
    TEMPORARY,

    /**
     * 孤立
     * 不保存实体数据、不处理实体逻辑、无法被代码搜索
     */
    ISOLATED,
}