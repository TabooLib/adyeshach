package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyPrimedTNT : AdyEntity() {

    open fun setFuseTime(fuseTime: Int) {
        setMetadata("fuseTime",fuseTime)
    }

    open fun getFuseTime():Int{
        return getMetadata("fuseTime")
    }
}