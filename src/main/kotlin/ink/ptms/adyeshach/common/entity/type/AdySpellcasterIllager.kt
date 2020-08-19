package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.util.BukkitUtils
import org.bukkit.entity.Spellcaster

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
open class AdySpellcasterIllager(entityTypes: EntityTypes) : AdyRaider(entityTypes) {

    init {
        /**
         * Spell (0: none, 1: summon vex, 2: attack, 3: wololo, 4: disappear, 5: blindness)
         */
        registerMeta(at(11400 to 16, 11200 to 15, 11100 to 13, 0 to -1), "spell", Spellcaster.Spell.NONE.ordinal)
    }

    fun setSpell(spell: Spellcaster.Spell) {
        setMetadata("spell", spell.ordinal)
    }

    fun getSpell(): Spellcaster.Spell {
        return BukkitUtils.valuesIllagerSpell()[getMetadata("spell")]
    }
}