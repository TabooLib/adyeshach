package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdySlime : AdyMob {

    fun getSize(): Int {
        return getMetadata("size")
    }

    fun setSize(size: Int) {
        setMetadata("size", size)
    }
}