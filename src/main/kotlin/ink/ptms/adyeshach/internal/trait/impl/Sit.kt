package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import ink.ptms.adyeshach.internal.trait.Trait
import org.bukkit.entity.Player

class Sit : Trait() {

    override fun getName(): String {
        return "sit"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        if (entityInstance.manager == null) {
            player.sendMessage("§c[Adyeshach] §7Entity Manager not initialized.")
            return
        }
        if (entityInstance.getVehicle()?.getCustomName() == "trait_sit_npc") {
            val vehicle = entityInstance.getVehicle()!!
            vehicle.removePassenger(entityInstance)
            vehicle.delete()
            player.sendMessage("§c[Adyeshach] §7Standing.")
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
            player.sendMessage("§c[Adyeshach] §7Sitting.")
        }
    }
}