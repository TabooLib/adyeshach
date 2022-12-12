package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyExperienceOrb
import org.bukkit.entity.Player

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
            spawn(viewer) {
                registerClientEntity(viewer)
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnEntityExperienceOrb(viewer, index, position.toLocation(), amount)
            }
        } else {
            super.visible(viewer, false)
        }
    }
}