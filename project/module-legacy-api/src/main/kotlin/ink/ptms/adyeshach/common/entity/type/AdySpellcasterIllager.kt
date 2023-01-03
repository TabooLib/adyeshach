package ink.ptms.adyeshach.common.entity.type


import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Spellcaster

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
@Deprecated("Outdated but usable")
abstract class AdySpellcasterIllager(entityTypes: EntityTypes, v2: ink.ptms.adyeshach.core.entity.EntityInstance) : AdyRaider(entityTypes, v2) {

    fun setSpell(spell: Spellcaster.Spell) {
        setMetadata("spell", spell.ordinal)
    }

    fun getSpell(): Spellcaster.Spell {
        return Spellcaster.Spell.values()[getMetadata("spell")]
    }
}