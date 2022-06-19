package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyPillager : AdyRaider {

    var isCharging: Boolean
        get() = getMetadata("isCharging")
        set(value) {
            setMetadata("isCharging", value)
        }
}