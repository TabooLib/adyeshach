package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author Arasple
 * @date 2020/8/4 22:18
 */
class AdyBee : AdyEntityAgeable(EntityTypes.BEE) {

    /**
     * 1.15+ 一致
     */
    init {
//        mask(at(11700 to 17, 11500 to 16), "unUsed", 0x01)
//        mask(at(11700 to 17, 11500 to 16), "isFlip", 0x02)
//        mask(at(11700 to 17, 11500 to 16), "hasStung", 0x04)
//        mask(at(11700 to 17, 11500 to 16), "hasNectar", 0x08)
//        natural(at(11700 to 18, 11500 to 17), "angerTicks", 0)
//                .canEdit(false)
//                .build()
//        naturalEditor("isAngered")
//                .reset { entity, _ ->
//                    (entity as AdyBee).setAngered(false)
//                }
//                .modify { player, entity, _ ->
//                    (entity as AdyBee).setAngered(!entity.isAngered())
//                    entity.openEditor(player)
//                }
//                .display { _, entity, _ ->
//                    (entity as AdyBee).isAngered().toDisplay()
//                }
    }

    fun setUnUsed(unused: Boolean) {
        setMetadata("unUsed", unused)
    }

    fun isUnUsed(): Boolean {
        return getMetadata("unUsed")
    }

    fun setFlip(anger: Boolean) {
        setMetadata("isFlip", anger)
    }

    fun isFlip(): Boolean {
        return getMetadata("isFlip")
    }

    fun setStung(stung: Boolean) {
        setMetadata("hasStung", stung)
    }

    fun hasStung(): Boolean {
        return getMetadata("hasStung")
    }

    fun setNectar(nectar: Boolean) {
        setMetadata("hasNectar", nectar)
    }

    fun hasNectar(): Boolean {
        return getMetadata("hasNectar")
    }

    fun setAngered(value: Boolean) {
        setMetadata("angerTicks", if (value) 999 else 0)
    }

    fun isAngered(): Boolean {
        return getMetadata<Int>("angerTicks") > 0
    }


}