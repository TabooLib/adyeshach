package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.bukkit.BukkitDragonPhase
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyEndDragon : AdyMob(EntityTypes.ENDER_DRAGON) {

    init {
        /*

        1.16,1.15
        15 ->Dragon phase
        1.14
        14 ->Dragon phase
        1.13,1.12,1.11,1.10
        12 ->Dragon phase
        1.9
        11 ->Dragon phase

         */
        registerMeta(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "dragonPhase", BukkitDragonPhase.HOVERING_WITH_NO_AI.ordinal)
                .from(Editors.enums(BukkitDragonPhase::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
                .display { _, entity, _ ->
                    Editor.toSimple(BukkitDragonPhase.values()[entity.getMetadata("dragonPhase")].name)
                }.build()
    }

    fun setDragonPhase(dragonPhase: BukkitDragonPhase) {
        setMetadata("dragonPhase", dragonPhase.ordinal)
    }

    fun getDragonPhase(): BukkitDragonPhase {
        return BukkitDragonPhase.of(getMetadata("dragonPhase"))
    }
}