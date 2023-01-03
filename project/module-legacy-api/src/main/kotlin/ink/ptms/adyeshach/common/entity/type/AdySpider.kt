package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author Arasple
 * @date 2020/8/5 22:35
 */
@Deprecated("Outdated but usable")
open class AdySpider(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyMob(entityTypes, v2) {

    constructor(v2: ink.ptms.adyeshach.core.entity.EntityInstance): this(EntityTypes.SPIDER, v2)

    fun setClimbing(climbing: Boolean) {
        setMetadata("isClimbing", climbing)
    }

    fun isClimbing(): Boolean {
        return getMetadata("isClimbing")
    }
}