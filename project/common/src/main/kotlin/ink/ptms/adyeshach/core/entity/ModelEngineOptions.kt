package ink.ptms.adyeshach.core.entity

import org.bukkit.Color

open class ModelEngineOptions {

    /** 是否使用状态机 */
    var useStateMachine = true

    /** 模型缩放比例 */
    var scale = 1.0

    /** 碰撞箱缩放比例 */
    var hitboxScale = 1.0

    /** 是否可以受到伤害 */
    var canHurt = true

    /** 默认的模型颜色 */
    var defaultTint: Color? = null

    /** 受伤时的模型颜色 */
    var damageTint: Color? = null

    /** 是否锁定俯仰角 */
    var isLockPitch = false

    /** 是否锁定偏航角 */
    var isLockYaw = false

    /** 是否自动初始化渲染器 */
    var isAutoRendererInitialization = true

    /** 是否显示碰撞箱 */
    var isHitboxVisible = true

    /** 是否显示阴影 */
    var isShadowVisible = true

    /** 是否覆盖原有的碰撞箱 */
    var isOverrideHitbox = true

    /**
     * 是否在不可见时销毁模型
     * 默认是开启的，如果实体有 HOLD/LOOP 动画，销毁后动画也会停止
     */
    var isDestroyWhenInvisible = true
}