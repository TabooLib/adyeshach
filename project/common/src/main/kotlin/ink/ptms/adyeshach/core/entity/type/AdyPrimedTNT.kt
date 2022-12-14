package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyPrimedTNT : AdyEntity {

    fun setFuseTime(fuseTime: Int) {
        setMetadata("fuseTime", fuseTime)
    }

    fun getFuseTime(): Int {
        return getMetadata("fuseTime")
    }
}