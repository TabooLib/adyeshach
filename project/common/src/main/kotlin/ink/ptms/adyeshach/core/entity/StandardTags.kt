package ink.ptms.adyeshach.core.entity

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.StandardTags
 *
 * @author 坏黑
 * @since 2022/10/18 09:13
 */
object StandardTags {

    /** 用于标记标签是否会被持久化储存 */
    annotation class PersistentTag

    /** 用于标记标签是否不会触发变更事件 */
    annotation class NoEvent

    /**
     * 衍生物
     * 持有该标签的单位不会在常规单位列表中显示，也无法通过常规手段编辑
     */
    @PersistentTag
    const val DERIVED = "DERIVED"

    /**
     * 孤立单位
     * 持有该标签的单位不属于任何管理器
     */
    @NoEvent
    const val ISOLATED = "ISOLATED"

    /**
     * 公共单位
     * 持有该标签的单位属于公共管理器
     */
    @NoEvent
    const val IS_PUBLIC = "IS_PUBLIC"

    /**
     * 私有单位
     * 持有该标签的单位属于私有管理器
     */
    @NoEvent
    const val IS_PRIVATE = "IS_PRIVATE"

    /**
     * 临时单位
     */
    @NoEvent
    const val IS_TEMPORARY = "IS_TEMPORARY"

    /**
     * 冻结状态
     * 持有该标签的单位无法移动
     */
    const val IS_FROZEN = "IS_FROZEN"

    /**
     * 移动状态
     * 持有该标签的单位正在移动
     */
    @NoEvent
    const val IS_MOVING = "IS_MOVING"

    /**
     * 移动准备状态
     * 持有该标签的单位正在移动
     */
    @NoEvent
    const val IS_MOVING_START = "IS_MOVING_START"

    /**
     * 位于载具中
     * 持有该标签的单位正在载具中
     */
    @PersistentTag
    const val IS_IN_VEHICLE = "IS_IN_VEHICLE"

    /**
     * Sit 状态
     */
    @PersistentTag
    const val IS_SITTING = "IS_SITTING"

    /**
     * 寻路状态
     * 持有该标签的单位正在寻路
     */
    const val IS_PATHFINDING = "IS_PATHFINDING"

    /**
     * 下一次的传送将不会检查位置变更
     */
    const val FORCE_TELEPORT = "FORCE_TELEPORT"
}