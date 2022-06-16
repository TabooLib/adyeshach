package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Suppress("SpellCheckingInspection")
abstract class AdyPufferfish : AdyFish() {

    open fun getPuffState(): Int {
        return getMetadata("puffState")
    }

    open fun setPuffState(value: Int) {
        setMetadata("puffState", value)
    }
}