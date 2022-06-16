package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyExperienceOrb : AdyEntity() {

    /**
     * 设置经验值
     */
    abstract fun setAmount(amount: Int)

    /**
     * 获取经验值
     */
    abstract fun getAmount(): Int
}