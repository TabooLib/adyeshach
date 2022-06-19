package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyExperienceOrb : AdyEntity {

    /**
     * 设置经验值
     */
    fun setAmount(amount: Int)

    /**
     * 获取经验值
     */
    fun getAmount(): Int
}