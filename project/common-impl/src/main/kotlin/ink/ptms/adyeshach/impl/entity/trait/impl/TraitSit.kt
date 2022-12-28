package ink.ptms.adyeshach.impl.entity.trait.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyArmorStand
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.impl.entity.trait.Trait
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent

object TraitSit : Trait() {

    @SubscribeEvent
    private fun onRemove(e: AdyeshachEntityRemoveEvent) {
        val vehicle = e.entity.getVehicle() ?: return
        if (vehicle.getCustomName() == "trait_sit_npc") {
            vehicle.remove()
        }
    }

    override fun getName(): String {
        return "sit"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        if (entityInstance.manager == null) {
            language.sendLang(player, "trait-sit-manager-not-set")
            return
        }
        if (entityInstance.getVehicle()?.getCustomName() == "trait_sit_npc") {
            val vehicle = entityInstance.getVehicle()!!
            vehicle.removePassenger(entityInstance)
            vehicle.remove()
            language.sendLang(player, "trait-sit-remove")
        } else {
            val inside = entityInstance.position.y.toString().substringAfter(".").startsWith("5")
            val sitNPC = entityInstance.manager!!.create(EntityTypes.ARMOR_STAND, entityInstance.getLocation().run {
                if (inside) {
                    this.y -= 0.4
                } else {
                    this.y -= 0.2
                }
                this
            }) as AdyArmorStand
            sitNPC.setCustomName("trait_sit_npc")
            sitNPC.setInvisible(true)
            sitNPC.setMarker(true)
            sitNPC.setSmall(true)
            sitNPC.addPassenger(entityInstance)
            language.sendLang(player, "trait-sit-create")
        }
    }
}