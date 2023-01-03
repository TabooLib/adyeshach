package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.impl.entity.trait.impl.setTraitTitle

@Deprecated("Outdated but usable", ReplaceWith("origin.setTraitTitle(title)", "ink.ptms.adyeshach.impl.entity.trait.impl.setTraitTitle"))
fun EntityInstance.setTraitTitle(title: List<String>?) {
    v2.setTraitTitle(title)
}