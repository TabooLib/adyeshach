package ink.ptms.adyeshach.impl.entity.trait.impl

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.type.AdyArmorStand
import ink.ptms.adyeshach.core.event.AdyeshachEntityHeadRotationEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityLoadedEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.impl.entity.trait.Trait
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.chat.uncolored
import taboolib.module.nms.MinecraftVersion
import java.util.concurrent.CompletableFuture

object TraitSit : Trait() {

    @SubscribeEvent
    private fun onRemove(e: AdyeshachEntityRemoveEvent) {
        val vehicle = e.entity.getVehicle() ?: return
        if (vehicle.getCustomName().uncolored() == "trait_sit_npc") {
            vehicle.remove()
        }
    }

    @SubscribeEvent
    private fun onCreate(e: AdyeshachEntityLoadedEvent) {
        if (e.entity.hasPersistentTag(StandardTags.IS_SITTING)) {
            e.entity.setTraitSit(true)
        }
    }

    @SubscribeEvent
    private fun onHeadRotation(e: AdyeshachEntityHeadRotationEvent) {
        if (e.entity.hasPersistentTag(StandardTags.IS_SITTING) && e.entity.hasPersistentTag(StandardTags.ENABLE_VEHICLE_ROTATION)) {
            e.entity.getVehicle()?.setHeadRotation(e.yaw, 0f, forceUpdate = true)
        }
    }

    override fun id(): String {
        return "sit"
    }

    override fun edit(player: Player, entityInstance: EntityInstance): CompletableFuture<Void> {
        if (entityInstance.manager == null) {
            error("Entity manager is null.")
        }
        if (entityInstance.getVehicle()?.getCustomName()?.uncolored() == "trait_sit_npc") {
            entityInstance.setTraitSit(false)
        } else {
            entityInstance.setTraitSit(true)
        }
        return CompletableFuture.completedFuture(null)
    }
}

/**
 * 是否在坐下
 */
fun EntityInstance.isTraitSit(): Boolean {
    return getVehicle()?.getCustomName()?.uncolored() == "trait_sit_npc"
}

/**
 * 切换坐下状态
 */
fun EntityInstance.setTraitSit(value: Boolean) {
    if (value) {
        setTraitSit(false)
        // 生成椅子
        manager!!.create(EntityTypes.ARMOR_STAND, getLocation().run {
            // 在 1.20.4 以下版本才进行微调
            if (MinecraftVersion.majorLegacy < 12004) {
                // 位于方块中间
                if (position.y.toString().substringAfter(".").startsWith("5")) {
                    this.y -= 0.4
                } else {
                    this.y -= 0.2
                }
            }
            this
        }) { chair ->
            chair as AdyArmorStand
            chair.setDerived("AdyeshachTrait")
            chair.setCustomName("trait_sit_npc")
            chair.setInvisible(true)
            chair.setMarker(true)
            chair.setSmall(true)
            chair.addPassenger(this)
        }
        setPersistentTag(StandardTags.IS_SITTING, "true")
    } else if (getVehicle()?.getCustomName()?.uncolored() == "trait_sit_npc") {
        val chair = getVehicle()!!
        chair.removePassenger(this)
        chair.remove()
        removePersistentTag(StandardTags.IS_SITTING)
    }
}