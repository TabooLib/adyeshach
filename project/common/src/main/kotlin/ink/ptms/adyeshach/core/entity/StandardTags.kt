package ink.ptms.adyeshach.core.entity

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.StandardTags
 *
 * @author 坏黑
 * @since 2022/10/18 09:13
 */
object StandardTags {

    /**
     * 衍生物
     * 持有该标签的单位不会在常规单位列表中显示，也无法通过常规手段编辑
     */
    const val DERIVED = "DERIVED"

    /**
     * 孤立单位
     * 持有该标签的单位不属于任何管理器
     */
    const val ISOLATED = "ISOLATED"

    /**
     * 公共单位
     * 持有该标签的单位属于公共管理器
     */
    const val IS_PUBLIC = "IS_PUBLIC"

    /**
     * 私有单位
     * 持有该标签的单位属于私有管理器
     */
    const val IS_PRIVATE = "IS_PRIVATE"

    /**
     * 临时单位
     */
    const val IS_TEMPORARY = "IS_TEMPORARY"

    /**
     * 冻结状态
     * 持有该标签的单位无法移动
     */
    const val IS_FREEZE = "IS_FREEZE"

    /**
     * 移动状态
     * 持有该标签的单位正在移动
     */
    const val IS_MOVING = "IS_MOVING"

    /**
     * 跳跃状态
     * 持有该标签的单位正在跳跃
     */
    const val IS_JUMPING = "IS_JUMPING"

    /**
     * 是否在地面
     * 持有该标签的单位正在地面
     */
    const val IS_ON_GROUND = "IS_ON_GROUND"

    /**
     * 位于载具中
     * 持有该标签的单位正在载具中
     */
    const val IS_IN_VEHICLE = "IS_IN_VEHICLE"

    /**
     * 寻路状态
     * 持有该标签的单位正在寻路
     */
    const val IS_PATHFINDING = "IS_PATHFINDING"
}