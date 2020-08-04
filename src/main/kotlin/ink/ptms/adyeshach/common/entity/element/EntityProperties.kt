package ink.ptms.adyeshach.common.entity.element

import io.izzel.taboolib.internal.gson.annotations.Expose

/**
 * @Author sky
 * @Since 2020-08-04 13:52
 */
open class EntityProperties(
        @Expose
        var onFire: Boolean = false,
        @Expose
        var crouched: Boolean = false,
        @Expose
        var unUsedRiding: Boolean = false,
        @Expose
        var sprinting: Boolean = false,
        @Expose
        var swimming: Boolean = false,
        @Expose
        var invisible: Boolean = false,
        @Expose
        var glowing: Boolean = false,
        @Expose
        var flyingElytra: Boolean = false
)