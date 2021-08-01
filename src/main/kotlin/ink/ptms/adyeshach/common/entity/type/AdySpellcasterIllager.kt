package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Spellcaster

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdySpellcasterIllager(entityTypes: EntityTypes) : AdyRaider(entityTypes) {

    init {
        /**
         * Spell (0: none, 1: summon vex, 2: attack, 3: wololo, 4: disappear, 5: blindness)
         */
        registerMeta(at(11700 to 17, 11400 to 16, 11200 to 15, 11100 to 13), "spell", Spellcaster.Spell.NONE.ordinal)
                .from(Editors.enums(Spellcaster.Spell::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
                .display { _, entity, _ ->
                    Spellcaster.Spell.values()[entity.getMetadata("spell")].name
                }.build()
    }

    fun setSpell(spell: Spellcaster.Spell) {
        setMetadata("spell", spell.ordinal)
    }

    fun getSpell(): Spellcaster.Spell {
        return Spellcaster.Spell.values()[getMetadata("spell")]
    }
}