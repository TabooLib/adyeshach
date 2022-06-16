package ink.ptms.adyeshach.common.entity.type

import org.bukkit.entity.Spellcaster

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Suppress("SpellCheckingInspection")
abstract class AdySpellcasterIllager : AdyRaider() {

    open fun setSpell(spell: Spellcaster.Spell) {
        setMetadata("spell", spell.ordinal)
    }

    open fun getSpell(): Spellcaster.Spell {
        return Spellcaster.Spell.values()[getMetadata("spell")]
    }
}