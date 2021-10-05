package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.api.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import ink.ptms.adyeshach.internal.trait.Trait
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.sendLang

object TraitSit : Trait() {

    @SubscribeEvent
    fun e(e: AdyeshachEntityRemoveEvent) {
        val vehicle = e.entity.getVehicle() ?: return
        if (vehicle.getCustomName() == "trait_sit_npc") {
            vehicle.delete()
        }
    }

    override fun getName(): String {
        return "sit"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        if (entityInstance.manager == null) {
            player.sendLang("trait-sit-manager-not-set")
            return
        }
        if (entityInstance.getVehicle()?.getCustomName() == "trait_sit_npc") {
            val vehicle = entityInstance.getVehicle()!!
            vehicle.removePassenger(entityInstance)
            vehicle.delete()
            player.sendLang("trait-sit-remove")
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
            player.sendLang("trait-sit-create")
        }
    }
}