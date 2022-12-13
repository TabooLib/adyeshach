package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityFireball

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
interface AdyWarden : AdyEntity, EntityFireball {

    fun getAngerLevel(): Int {
        return getMetadata("angerLevel")
    }

    fun setAngerLevel(level: Int) {
        setMetadata("angerLevel", level)
    }
}