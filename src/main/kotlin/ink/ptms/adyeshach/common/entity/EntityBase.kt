package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.entity.element.EntityProperties
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.position.Position
import ink.ptms.adyeshach.nms.NMS
import io.izzel.taboolib.internal.gson.annotations.Expose

/**
 * @Author sky
 * @Since 2020-08-04 12:47
 */
abstract class EntityBase(@Expose val entityType: EntityTypes) {

    @Expose
    var world: String? = null
        protected set

    @Expose
    var position = Position.empty()
        protected set
        get() = field.clone()

    @Expose
    protected val properties = EntityProperties()

    protected fun initialize(world: String, position: Position) {
        this.world = world
        this.position = position
    }
}