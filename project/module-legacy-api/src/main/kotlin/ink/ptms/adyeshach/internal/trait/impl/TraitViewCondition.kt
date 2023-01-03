package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.impl.entity.trait.impl.setTraitViewCondition
import ink.ptms.adyeshach.impl.entity.trait.impl.updateTraitViewCondition

@Deprecated("Outdated but usable", ReplaceWith("origin.setTraitViewCondition(condition)", "ink.ptms.adyeshach.impl.entity.trait.impl.setTraitViewCondition"))
fun EntityInstance.setViewCondition(condition: List<String>?) {
    v2.setTraitViewCondition(condition)
}

@Deprecated("Outdated but usable", ReplaceWith("origin.updateTraitViewCondition()", "ink.ptms.adyeshach.impl.entity.trait.impl.updateTraitViewCondition"))
fun EntityInstance.updateViewCondition() {
    v2.updateTraitViewCondition()
}