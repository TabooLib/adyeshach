package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyExperienceOrb
import ink.ptms.adyeshach.impl.util.ifTrue
import org.bukkit.entity.Player
import taboolib.common5.cint

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultExperienceOrb
 *
 * @author 坏黑
 * @since 2022/6/29 19:03
 */
abstract class DefaultExperienceOrb(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyExperienceOrb {

    @Expose
    private var amount = 1

    override fun setAmount(amount: Int) {
        this.amount = amount
    }

    override fun getAmount(): Int {
        return amount
    }

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            prepareSpawn(viewer) {
                registerClientEntity(viewer)
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityExperienceOrb(viewer, index, position.toLocation(), amount)
            }
        } else {
            super.visible(viewer, false)
        }
    }

    override fun setCustomMeta(key: String, value: String): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "amount" -> {
                setAmount(value.cint)
                true
            }
            else -> false
        }
    }
}