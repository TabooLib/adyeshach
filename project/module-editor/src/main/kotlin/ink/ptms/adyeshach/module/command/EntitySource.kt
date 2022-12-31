package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.core.entity.EntityInstance

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.command.EntitySource
 *
 * @author 坏黑
 * @since 2022/12/23 19:23
 */
abstract class EntitySource(val elements: MutableList<EntityInstance>) {

    abstract fun isUpdated(entity: EntityInstance): Boolean

    abstract fun extraArgs(entity: EntityInstance): Array<Any>

    class Empty(elements: MutableList<EntityInstance>) : EntitySource(elements) {

        override fun isUpdated(entity: EntityInstance): Boolean {
            return false
        }

        override fun extraArgs(entity: EntityInstance): Array<Any> {
            return emptyArray()
        }
    }
}