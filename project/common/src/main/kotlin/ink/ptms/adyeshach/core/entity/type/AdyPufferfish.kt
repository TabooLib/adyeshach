package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyPufferfish : AdyFish {

    fun getPuffState(): Int {
        return getMetadata("puffState")
    }

    fun setPuffState(value: Int) {
        setMetadata("puffState", value)
    }
}